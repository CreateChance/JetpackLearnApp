package com.example.coroutine

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

/**
 * 每日自动刷新 title 的 worker
 * 这里需要继承自 [CoroutineWorker] 才能将 doWork 方法定义成 suspend 函数，以支持协程调用
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/14
 */
class DailyRefreshTitleWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val database = getTitleDatabase(applicationContext)
        val repository = MyRepository(titleService, database.titleDao)

        return try {
            repository.refreshTitle()
            Result.success()
        } catch (e: TitleRefreshError) {
            Result.failure()
        }
    }

    class Factory : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return DailyRefreshTitleWorker(appContext, workerParameters)
        }
    }
}
