package com.example.hilt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 自定义 application，方便 hilt 注入
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/4
 */
@HiltAndroidApp
class DemoApplication : Application() {
}