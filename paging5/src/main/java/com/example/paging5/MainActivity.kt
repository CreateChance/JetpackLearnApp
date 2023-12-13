package com.example.paging5

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingDataAdapter
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import kotlin.math.sin
import androidx.room.Query as RoomQuery
import retrofit2.http.Query as RetrofitQuery

/**
 * 采用最新的 Paging 接入方式使用
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MyViewModel
    private lateinit var recyclerAdapter: SubjectPagedListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = SubjectPagedListAdapter(this)
        recyclerView.adapter = recyclerAdapter

        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        lifecycleScope.launch {
            viewModel.subjectFlow.collectLatest { pagingData ->
                recyclerAdapter.submitData(pagingData)
            }
        }

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                SubjectDatabase.getInstance(application).getSubjectDao().clearAll()
                swipeRefreshLayout.isRefreshing = false
            }
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subjects: List<Subject>)

    @RoomQuery("DELETE FROM movie")
    suspend fun clearAll()

    @RoomQuery("SELECT * FROM movie")
    fun pagingSource(): PagingSource<Int, Subject>
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
    suspend fun getResult(
        @RetrofitQuery("since") since: Int,
        @RetrofitQuery("pagesize") pageSize: Int
    ): List<Subject>
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

class SubjectPagedListAdapter(
    private val context: Context,
) : PagingDataAdapter<Subject, SubjectPagedListAdapter.MyViewHolder>(object :
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

class MyViewModel(application: Application) : AndroidViewModel(application) {

    private val config = PagingConfig(
        enablePlaceholders = false,
        pageSize = SubjectPagingSource.pageSize,
        prefetchDistance = 2,
        initialLoadSize = SubjectPagingSource.pageSize * 2,
        maxSize = 20000,
    )

    @OptIn(ExperimentalPagingApi::class)
    val pager = Pager(
        config = config,
        remoteMediator = SubjectRemoteMediator(
            0,
            SubjectDatabase.getInstance(application),
            RetrofitClient.getServerApi()
        )
    ) {
        SubjectDatabase.getInstance(application).getSubjectDao().pagingSource()
    }

    // 数据流
    val subjectFlow = pager.flow.cachedIn(viewModelScope)
}

@OptIn(ExperimentalPagingApi::class) // 现在还是实验性接口，不建议在产品形态中使用
class SubjectRemoteMediator(
    private val since: Int,
    private val database: SubjectDatabase,
    private val serverApi: IServerApi
) : RemoteMediator<Int, Subject>() {

    private val subjectDao = database.getSubjectDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Subject>
    ): MediatorResult {
        return try {
            // The network load method takes an optional after=<user.id>
            // parameter. For every page after the first, pass the last user
            // ID to let it continue from where it left off. For REFRESH,
            // pass null to load the first page.
            Log.d("GAOCHAO", "开始装载页面数据, loadType: $loadType")
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = false)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    lastItem.id
                }
            }

            // Suspending network load via Retrofit. This doesn't need to be
            // wrapped in a withContext(Dispatcher.IO) { ... } block since
            // Retrofit's Coroutine CallAdapter dispatches on a worker
            // thread.
            Log.d("GAOCHAO", "正在装载页面数据，since: ${loadKey ?: since}")
            val response = serverApi.getResult(loadKey ?: since, SubjectPagingSource.pageSize)

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    subjectDao.clearAll()
                }

                // Insert new subjects into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                subjectDao.insertAll(response)
            }

            if (response.isEmpty()) {
                Log.d("GAOCHAO", "数据到底啦！没有更多数据了")
            }
            MediatorResult.Success(
                endOfPaginationReached = response.isEmpty()
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}

class SubjectPagingSource : PagingSource<Int, Subject>() {
    companion object {
        const val firstSinceIndex = 0
        const val pageSize = 10
    }

    override fun getRefreshKey(state: PagingState<Int, Subject>): Int? {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        val result = state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
        Log.d("GAOCHAO", "Refresh Key: $result")
        return result
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Subject> {
        return try {
            val nextPageNumber = params.key ?: firstSinceIndex
            val subjects = RetrofitClient.getServerApi().getResult(nextPageNumber, pageSize)
            Log.d("GAOCHAO", "请求新页面数据成功。")
            LoadResult.Page(
                data = subjects,
                prevKey = null, // Only paging forward.
                nextKey = subjects.last().id // 用左右一个 subject 的 id 作为下一页的 since
            )
        } catch (e: Exception) {
            Log.d("GAOCHAO", "糟糕，网络请求失败了! $e")
            LoadResult.Error(e)
        }
    }
}
