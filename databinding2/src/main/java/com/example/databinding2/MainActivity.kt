package com.example.databinding2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.databinding2.databinding.ActivityMainBinding

/**
 * 演示 main 布局和 sub 布局之间的 data binding 数据传递问题
 * 通过两个布局之间定义相同的数据类型，然后通过 app:idol 来传递数据到子页面
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        dataBinding.idol = Idol("斯佳丽.约翰逊", 3)
    }
}

class Idol(
    val name: String,
    val star: Int,
)

class StarUtils {
    companion object {
        @JvmStatic
        fun getStart(star: Int): String {
            return when (star) {
                1 -> "一星"
                2 -> "二星"
                3 -> "三星"
                4 -> "四星"
                5 -> "五星"
                else -> ""
            }
        }
    }
}
