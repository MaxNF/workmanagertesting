package com.example.work_manager_testing.utils

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.work_manager_testing.domain.Repository
import com.example.work_manager_testing.workers.TimeConsumingWorker
import com.example.work_manager_testing.workers.TimeConsumingWorker2

class TestWorkerFactory(private val repository: Repository) : WorkerFactory() {
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
        return when (workerClassName) {
            TimeConsumingWorker::class.java.name -> TimeConsumingWorker(appContext, workerParameters, repository)
            TimeConsumingWorker2::class.java.name -> TimeConsumingWorker2(appContext, workerParameters, repository)
            else -> null
        }
    }
}