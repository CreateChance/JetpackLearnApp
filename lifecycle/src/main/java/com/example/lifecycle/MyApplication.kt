package com.example.lifecycle

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/5
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 进程启动的时候添加监听器
        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationObserver())
    }
}

// application 生命周期监听器
class ApplicationObserver : DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        Log.d("GAOCHAO", "Application onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("GAOCHAO", "Application onStart")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        Log.d("GAOCHAO", "Application onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        Log.d("GAOCHAO", "Application onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("GAOCHAO", "Application onStop")
    }

    // 这个永远不会调用，app 会直接退出。
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        Log.d("GAOCHAO", "Application onDestroy")
    }
}
