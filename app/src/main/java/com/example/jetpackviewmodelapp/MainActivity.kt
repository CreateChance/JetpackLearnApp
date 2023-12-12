package com.example.jetpackviewmodelapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * 最原始的 view model 使用
 */
class MainActivity : AppCompatActivity() {
    private lateinit var mTvResult: TextView
    private lateinit var myViewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTvResult = findViewById(R.id.textView)

        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        mTvResult.text = myViewModel.number.toString()
    }

    fun add(view: View) {
        mTvResult.text = (++myViewModel.number).toString()
    }
}

/**
 * 1. 不要向 view model 传入 context，会导致内存泄漏
 * 2. 如果要使用 context，请继承自 AndroidViewModel，然后使用传入的 Application
 */
class MyViewModel : ViewModel() {
    public var number: Int = 0
}
