package com.example.paging4

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.room.Query as RoomQuery
import retrofit2.http.Query as RetrofitQuery

/**
 * 演示 paging BoundaryCallback 的使用，配合 Room + Network 进行二级数据访问架构的实现
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SubjectPagedListAdapter(this)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        viewModel.subjectPagedList.observe(this) {
            adapter.submitList(it)
        }

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.clearAndRefresh()
            swipeRefreshLayout.isRefreshing = false
        }
    }
}

@Entity(tableName = "movie")
data class Subject(

    @ColumnInfo(name = "db_key", typeAffinity = ColumnInfo.INTEGER)
    @PrimaryKey(autoGenerate = true)
    val dbKey: Int, // 新建字段表示 db 的主键，不要用下面的 id，下面的 id 是原始数据的字段，不要做数据库检索用

    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    @SerializedName("id")
    val id: Int,

    @ColumnInfo(name = "title", typeAffinity = ColumnInfo.TEXT)
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "cover", typeAffinity = ColumnInfo.TEXT)
    @SerializedName("cover")
    val cover: String,

    @ColumnInfo(name = "rate", typeAffinity = ColumnInfo.TEXT)
    @SerializedName("rate")
    val rate: String
)

@Dao
interface SubjectDao {

    @Insert
    fun insertSubjects(subjects: List<Subject>)

    @RoomQuery("DELETE FROM movie")
    fun clear()

    @RoomQuery("SELECT * FROM movie")
    fun getSubjectList(): DataSource.Factory<Int, Subject>
}

@Database(entities = [Subject::class], version = 1, exportSchema = true)
abstract class SubjectDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "my_db.db"

        private lateinit var INSTANCE: SubjectDatabase

        @Synchronized
        fun getInstance(context: Context): SubjectDatabase {
            if (!::INSTANCE.isInitialized) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    SubjectDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // 当升级数据库时发生的任何异常都会销毁数据库并且重新建立一个空的表，避免崩溃
                    .build()
            }
            return INSTANCE
        }
    }

    abstract fun getSubjectDao(): SubjectDao
}

interface IServerApi {
    @GET("ikds.do")
    fun getResult(
        @RetrofitQuery("since") since: Int,
        @RetrofitQuery("pagesize") pageSize: Int
    ): Call<List<Subject>>
}

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.71.62.58:8080/com.dongnaoedu/")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getServerApi(): IServerApi {
        return retrofit.create(IServerApi::class.java)
    }
}

class MyViewModel(application: Application) : AndroidViewModel(application) {

    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false) // 设置控件占位，默认 true
        .setPageSize(SubjectBoundaryCallback.pageSize) // 设置一页的大小
        .setPrefetchDistance(2) // 距离底部还有 2 条数据时，开始加载下一页
        .setInitialLoadSizeHint(SubjectBoundaryCallback.pageSize * 2) // 设置初始装在条数，一般为页大小的 2 倍
        .setMaxSize(20000) // 设置最大条数
        .build()

    val subjectPagedList: LiveData<PagedList<Subject>> = LivePagedListBuilder(
        SubjectDatabase.getInstance(application).getSubjectDao().getSubjectList(),
        config
    )
        .setBoundaryCallback(SubjectBoundaryCallback(application))
        .build()

    /**
     * 清空所有数据，并且重新从网络获取数据
     */
    fun clearAndRefresh() {
        AsyncTask.execute {
            SubjectDatabase.getInstance(getApplication()).getSubjectDao().clear()
        }
    }

}

class SubjectPagedListAdapter(
    private val context: Context,
) : PagedListAdapter<Subject, SubjectPagedListAdapter.MyViewHolder>(object :
    DiffUtil.ItemCallback<Subject>() {
    override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
        return oldItem == newItem
    }
}) {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mIvCover: ImageView
        var mTvTitle: TextView
        var mTvRate: TextView

        init {
            this.mIvCover = itemView.findViewById(R.id.iv_cover)
            this.mTvTitle = itemView.findViewById(R.id.tv_title)
            this.mTvRate = itemView.findViewById(R.id.tv_rate)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val subject = getItem(position) ?: return
        holder.mIvCover.load(subject.cover) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_foreground) // 当网络图片装载失败的时候，默认使用本地的资源文件
            transformations(CircleCropTransformation())
        }
        holder.mTvTitle.text = subject.title
        holder.mTvRate.text = "评分：${subject.rate}"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false))
    }
}

class SubjectBoundaryCallback(private val application: Application) :
    PagedList.BoundaryCallback<Subject>() {

    companion object {
        const val firstSinceIndex = 0
        const val pageSize = 10
    }

    // 加载首页数据
    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        Log.d("GAOCHAO", "请求首页")
        RetrofitClient.getServerApi().getResult(firstSinceIndex, pageSize)
            .enqueue(object : Callback<List<Subject>> {
                override fun onResponse(
                    call: Call<List<Subject>>,
                    response: Response<List<Subject>>
                ) {
                    Log.d("GAOCHAO", "网络请求返回了！")
                    if (response.body() != null) {
                        // 存储数据到数据库中
                        insertSubjectsToDatabase(response.body()!!)
                    } else {
                        Log.d("GAOCHAO", "哦吼，返回的 body 为 null")
                    }
                }

                override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                    Log.d("GAOCHAO", "糟糕，网络请求失败了！")
                    t.printStackTrace()
                }
            })
    }

    // 加载下一页数据
    override fun onItemAtEndLoaded(itemAtEnd: Subject) {
        super.onItemAtEndLoaded(itemAtEnd)

        Log.d("GAOCHAO", "请求下一页, item: $itemAtEnd")
        RetrofitClient.getServerApi().getResult(itemAtEnd.id, pageSize)
            .enqueue(object : Callback<List<Subject>> {
                override fun onResponse(
                    call: Call<List<Subject>>,
                    response: Response<List<Subject>>
                ) {
                    Log.d("GAOCHAO", "网络请求返回了！")
                    if (response.body() != null) {
                        insertSubjectsToDatabase(response.body()!!)
                    } else {
                        Log.d("GAOCHAO", "哦吼，返回的 body 为 null")
                    }
                }

                override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                    Log.d("GAOCHAO", "糟糕，网络请求失败了！")
                    t.printStackTrace()
                }
            })
    }

    private fun insertSubjectsToDatabase(subjects: List<Subject>) {
        AsyncTask.execute {
            SubjectDatabase.getInstance(application).getSubjectDao().insertSubjects(subjects)
        }
    }
}
