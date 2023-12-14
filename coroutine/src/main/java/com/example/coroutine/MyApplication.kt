package com.example.coroutine

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * 自定义 application
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/14
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        setupWorkManager()
    }

    private fun setupWorkManager() {
        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(DailyRefreshTitleWorker.Factory())
            .build()

        WorkManager.initialize(this, workManagerConfig)

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val dailyRefreshTitleWorker =
            PeriodicWorkRequestBuilder<DailyRefreshTitleWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        // enqueue workers to run them.
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DailyRefreshTitleWorker::class.java.name,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyRefreshTitleWorker
        )
    }
}