package com.example.jetpackviewmodelapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.Timer
import java.util.TimerTask

/**
 * View model 和 livedata 结合使用，来更新界面
 */
class MainActivity2 : AppCompatActivity() {
    private lateinit var mTvResult: TextView
    private lateinit var myViewModel: MyViewModel2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        mTvResult = findViewById(R.id.textView)

        myViewModel = ViewModelProvider(this)[MyViewModel2::class.java]

        mTvResult.text = myViewModel.number.value.toString()

        // 关键步骤，添加 live data 的监听器，当数据变化的时候更新到 UI 组件
        myViewModel.number.observe(this) {
            mTvResult.text = it.toString()
        }

        startTimer()
    }

    private fun startTimer() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                // 发布变化的数据
                // 注意：UI 线程更新使用 setValue，非 UI 线程更新使用 postValue
                myViewModel.number.postValue(myViewModel.number.value!! + 1)
            }
        }, 1000, 1000)
    }
}

class MyViewModel2 : ViewModel() {
    var number: MutableLiveData<Int> = MutableLiveData(0)
}

