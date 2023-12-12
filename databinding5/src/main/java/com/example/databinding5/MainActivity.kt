package com.example.databinding5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.databinding5.databinding.ActivityMainBinding
import com.example.databinding5.databinding.ItemBinding

/**
 * 演示 recyclerview 的绑定操作
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        dataBinding.recyclerview.adapter = RecyclerViewAdapter(
            listOf(
                Idol(
                    "中文1",
                    "English1",
                    "https://pic4.zhimg.com/80/v2-af366d48f98307a60991ef3a22f7219f_1440w.webp"
                ),
                Idol(
                    "中文2",
                    "English2",
                    "https://pic4.zhimg.com/80/v2-305d1e4011a1260d9e084be4a9d0370b_1440w.webp"
                ),
                Idol(
                    "中文3",
                    "English3",
                    "https://pic2.zhimg.com/80/v2-73bf98cb106bf5c75ec87c53e27eb17d_1440w.webp"
                ),
                Idol(
                    "中文4",
                    "English4",
                    "https://pic2.zhimg.com/80/v2-977a3db3a15ce01eb8ca09677b453cf1_1440w.webp"
                ),
                Idol(
                    "中文5",
                    "English5",
                    "https://pic3.zhimg.com/80/v2-d9f108ebf056b6346b5ebcb6f890c646_1440w.webp"
                ),
                Idol(
                    "中文6",
                    "English6",
                    "https://pic1.zhimg.com/80/v2-07ce02e5b8d040d500e3acaebc012b30_1440w.webp"
                ),
                Idol(
                    "中文7",
                    "English7",
                    "https://pic1.zhimg.com/80/v2-9476815e6d7f20725f3786126ecbe900_1440w.webp"
                ),
                Idol(
                    "中文8",
                    "English8",
                    "https://pic1.zhimg.com/80/v2-3f7708a8839ee8a2cb56dca080db0518_1440w.webp"
                ),
                Idol(
                    "中文9",
                    "English9",
                    "https://pic3.zhimg.com/80/v2-78989431b851b49f47c426da8fba791e_1440w.webp"
                ),
                Idol(
                    "中文10",
                    "English10",
                    "https://pic1.zhimg.com/80/v2-be519a2fb6f86ca2b4b31cfc0e630e50_1440w.webp"
                ),
            )
        )
        dataBinding.recyclerview.layoutManager = LinearLayoutManager(this)
    }
}

class Idol(
    val chName: String,
    val enName: String,
    val image: String
)

class ImageViewBindingAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrl"], requireAll = false)
        fun setImage(imageView: ImageView, url: String) {
            imageView.load(url) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_foreground) // 当网络图片装载失败的时候，默认使用本地的资源文件
                transformations(CircleCropTransformation())
            }
        }
    }
}

class RecyclerViewAdapter(private val idols: List<Idol>) :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var itemBinding: ItemBinding

        constructor(itemBinding: ItemBinding) : this(itemBinding.root) {
            this.itemBinding = itemBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding = DataBindingUtil.inflate<ItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item,
            parent,
            false
        )
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemBinding.idol = idols[position]
    }

    override fun getItemCount(): Int {
        return idols.size
    }
}
