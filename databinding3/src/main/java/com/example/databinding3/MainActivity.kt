package com.example.databinding3

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.example.databinding3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        dataBinding.networkImage =
            "https://pic4.zhimg.com/100/v2-2bdb298a82e60e3385c781738523512d_r.jpg"
        dataBinding.localImage = R.drawable.local_image
    }
}

class ImageViewBindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter("image")
        fun setImage(imageView: ImageView, url: String) {
            Log.d("GAOCHAO", "111111111111")
            imageView.load(url) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                transformations(CircleCropTransformation())
            }
        }

        @JvmStatic
        @BindingAdapter("image")
        fun setImage(imageView: ImageView, resId: Int) {
            Log.d("GAOCHAO", "222222222222")
            imageView.setImageResource(resId)
            imageView.load(resId) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                transformations(CircleCropTransformation())
            }
        }

        @JvmStatic
        @BindingAdapter(
            value = ["image", "defaultImageResource"], /* 布局文件中可以使用这里的名称来定义图片资源 */
            requireAll = false /* 这里表示并不要求所有的参数都提供 */
        )
        fun setImage(imageView: ImageView, url: String, resId: Int) {
            Log.d("GAOCHAO", "333333333333")
            imageView.load(url) {
                crossfade(true)
                memoryCachePolicy(CachePolicy.DISABLED) // 为了测试方便，这里关闭内存缓存
                diskCachePolicy(CachePolicy.DISABLED) // 为了测试方便，这里关闭磁盘缓存
                placeholder(R.drawable.ic_launcher_background)
                error(resId) // 当网络图片装载失败的时候，默认使用本地的资源文件
                transformations(CircleCropTransformation())
            }
        }
    }
}
