package com.example.workmanager2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.Duration

/**
 * 演示 Work 的工作链使用
 * 工作链：
 *     AWorker ---> BWorker
 *                          \
 *                           \
 *                           / --> EWorker
 *                          /
 *     CWorker ---> DWorker
 * 工作链中，前一个 worker 的输出参数，就是后一个 worker 的输入参数
 */
class MainActivity : AppCompatActivity() {

    private val workTag = "MyWorkRequest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startWork(view: View) {
        val aWorker = OneTimeWorkRequestBuilder<AWorker>()
            .setInputData(Data.Builder().putString("work_input1", "awork input data").build())
            .build()
        val bWorker = OneTimeWorkRequestBuilder<BWorker>()
            .build()
        val cWorker = OneTimeWorkRequestBuilder<CWorker>()
            .setInputData(Data.Builder().putString("work_input2", "cwork input data").build())
            .build()
        val dWorker = OneTimeWorkRequestBuilder<DWorker>()
            .build()
        val eWorker = OneTimeWorkRequestBuilder<EWorker>()
            .setInitialDelay(Duration.ofSeconds(5))
            .addTag(workTag)
            .build()

        val workContinuation1 = WorkManager.getInstance(this)
            .beginWith(aWorker)
            .then(bWorker)

        val workContinuation2 = WorkManager.getInstance(this)
            .beginWith(cWorker)
            .then(dWorker)

        WorkContinuation.combine(listOf(workContinuation1, workContinuation2))
            .then(eWorker)
            .enqueue()
    }

    fun stopWork(view: View) {
        WorkManager.getInstance(this).cancelAllWorkByTag(workTag)
    }
}

class AWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("GAOCHAO", "${javaClass.canonicalName} doWork.")
        return Result.success(
            Data.Builder().putString("work_input1", "$${javaClass.canonicalName} output data.")
                .build()
        )
    }
}

class BWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d(
            "GAOCHAO",
            "${javaClass.canonicalName} doWork, input data: ${inputData.getString("work_input1")}"
        )
        return Result.success(
            Data.Builder().putString("work_input1", "$${javaClass.canonicalName} output data.")
                .build()
        )
    }
}

class CWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("GAOCHAO", "${javaClass.canonicalName} doWork.")
        return Result.success(
            Data.Builder().putString("work_input2", "$${javaClass.canonicalName} output data.")
                .build()
        )
    }
}

class DWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d(
            "GAOCHAO",
            "${javaClass.canonicalName} doWork, input data: ${inputData.getString("work_input2")}"
        )
        return Result.success(
            Data.Builder().putString("work_input2", "$${javaClass.canonicalName} output data.")
                .build()
        )
    }
}

class EWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("GAOCHAO", "${javaClass.canonicalName} doWork. input data: ${inputData}")
        return Result.success()
    }
}
