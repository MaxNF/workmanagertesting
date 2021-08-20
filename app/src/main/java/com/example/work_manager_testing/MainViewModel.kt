package com.example.work_manager_testing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.example.work_manager_testing.workers.TimeConsumingWorker
import com.example.work_manager_testing.workers.TimeConsumingWorker2
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {

    companion object {
        const val WORK_UNIQUE_NAME = "work"
    }

    fun planWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequestFirst = OneTimeWorkRequestBuilder<TimeConsumingWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        val workRequestSecond = OneTimeWorkRequestBuilder<TimeConsumingWorker2>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .beginUniqueWork(WORK_UNIQUE_NAME, ExistingWorkPolicy.KEEP, workRequestFirst)
            .then(workRequestSecond)
            .enqueue()
    }
}