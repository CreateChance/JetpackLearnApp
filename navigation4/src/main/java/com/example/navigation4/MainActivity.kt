package com.example.navigation4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 研究 navigation 多 fragment 跳转堆栈以及生命周期问题
 *
 * Fragment lifecycle: https://developer.android.com/guide/fragments/lifecycle?hl=zh-cn
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}