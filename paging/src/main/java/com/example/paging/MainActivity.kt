package com.example.paging

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.paging.PositionalDataSource
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import retrofit2.http.Query

/**
 * 演示 Paging PositionalDataSource 的基本使用
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SubjectPagedListAdapter(this)
        recyclerView.adapter = adapter

        val viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        viewModel.subjectPagedList.observe(this) {
            adapter.submitList(it)
        }
    }
}

data class Subject(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("cover")
    val cover: String,

    @SerializedName("rate")
    val rate: String
)

data class Result(
    @SerializedName("count")
    val count: Int,

    @SerializedName("start")
    val start: Int,

    @SerializedName("total")
    val total: Int,

    @SerializedName("subjects")
    val subjects: List<Subject>
)

interface IServerApi {
    @GET("pds.do")
    fun getResult(@Query("start") start: Int, @Query("count") count: Int): Call<Result>
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

class ResultDataSource : PositionalDataSource<Subject>() {

    companion object {
        const val pageSize = 9
    }

    // 页面首次加载数据调用
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Subject>) {
        RetrofitClient.getServerApi().getResult(0, pageSize).enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                Log.d("GAOCHAO", "网络请求返回了！")
                if (response.body() != null) {
                    callback.onResult(
                        response.body()!!.subjects,
                        response.body()!!.start,
                        response.body()!!.total
                    )
                } else {
                    Log.d("GAOCHAO", "哦吼，返回的 body 为 null")
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.d("GAOCHAO", "糟糕，网络请求失败了！")
                t.printStackTrace()
            }
        })
    }

    // 加载下一页
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Subject>) {
        RetrofitClient.getServerApi().getResult(params.startPosition, pageSize)
            .enqueue(object : Callback<Result> {
                override fun onResponse(call: Call<Result>, response: Response<Result>) {
                    if (response.body() != null) {
                        callback.onResult(response.body()!!.subjects)
                        Log.d(
                            "GAOCHAO",
                            "成功装载新数据，位置: ${params.startPosition}, size: ${params.loadSize}"
                        )
                    }
                }

                override fun onFailure(call: Call<Result>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }
}

class ResultDataSourceFactory : DataSource.Factory<Int, Subject>() {
    override fun create(): DataSource<Int, Subject> {
        return ResultDataSource()
    }
}

class MyViewModel : ViewModel() {

    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false) // 设置控件占位，默认 true
        .setPageSize(ResultDataSource.pageSize) // 设置一页的大小
        .setPrefetchDistance(2) // 距离底部还有 2 条数据时，开始加载下一页
        .setInitialLoadSizeHint(ResultDataSource.pageSize * 2) // 设置初始装在条数，一般为页大小的 2 倍
        .setMaxSize(20000) // 设置最大条数
        .build()

    val subjectPagedList: LiveData<PagedList<Subject>> = LivePagedListBuilder(
        ResultDataSourceFactory(),
        config
    ).build()

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

