package com.example.databinding

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.databinding.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        dataBinding.idol = Idol("斯佳丽.约翰逊", 4)
        dataBinding.eventHandler = EventHandlerListener(this)
    }
}

class Idol(
    val name: String,
    val star: Int,
)

class EventHandlerListener(private val context: Context) {
    fun buttonOnClick(view: View) {
        Toast.makeText(context, "喜欢", Toast.LENGTH_SHORT).show()
    }
}

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
