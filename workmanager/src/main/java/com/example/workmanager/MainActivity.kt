package com.example.workmanager

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.Duration

/**
 * 演示 work manger 的基本使用方式
 */
class MainActivity : AppCompatActivity() {

    private val workRequestTag = "WorkRequest1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun submitTask(view: View) {
        // 任务的执行时机是不可预期的，实际的执行点是系统内部策略，可能立即执行，也可能不立即执行。
        // 设置触发条件
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 配置任务
        // 一次性执行的任务
        val workRequest = OneTimeWorkRequestBuilder<MyWork>()
            .setConstraints(constraints)
            .setInitialDelay(Duration.ofSeconds(5)) // 在触发条件到达之后，延迟 5s 之后在执行
            .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofSeconds(2)) // 在失败之后重试的退避策略
            .addTag(workRequestTag)
            .setInputData(Data.Builder().putString("input_data", "aaaa").build()) // 设置 Task 入参
            .build()

        // 周期性任务，调度间隔不得低于 15min
        val workRequest2 = PeriodicWorkRequestBuilder<MyWork>(Duration.ofMinutes(15))
            .build()

        // 提交任务给 WorkManager 来执行
        WorkManager.getInstance(this).enqueue(workRequest)

        // 观察 work 执行的状态
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id).observe(this) {
            Log.d("GAOCHAO", "监听到 work 状态：$it")
            // 可以根据状态来获取输出的参数
            if (it.state == WorkInfo.State.SUCCEEDED) {
                Log.d("GAOCHAO", "自定义任务输出的参数：${it.outputData.getString("output_data")}")
            }
        }
    }

    fun cancelTask(view: View) {
        WorkManager.getInstance(this).cancelAllWorkByTag(workRequestTag)
    }
}

class MyWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val inputData = inputData.getString("input_data")
        Log.d("GAOCHAO", "自定义任务正在执行中，输入的参数：$inputData")
        SystemClock.sleep(2000)
        Log.d("GAOCHAO", "自定义任务完成啦！")
        return Result.success(Data.Builder().putString("output_data", "bbbb").build()) // 定义输出参数
    }

}

