package com.example.work_manager_testing.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.work_manager_testing.data.CallResult
import com.example.work_manager_testing.data.RepositoryImpl
import com.example.work_manager_testing.domain.Repository
import kotlinx.coroutines.Dispatchers

class TimeConsumingWorker2(appContext: Context, params: WorkerParameters, private val repository: Repository) :
    CoroutineWorker(appContext, params) {

    companion object {
        private const val TAG = "TimeConsumingWorker2"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork: initiated")
        val inputInt = inputData.getInt("key", -1)
        if (inputInt == -1) return Result.failure()
        return when (val result = repository.performLongRunningTaskAndGetRandomInt()) {
            is CallResult.Success -> {
                val data = Data.Builder().putInt("key", result.value).build()
                Log.d(TAG, "doWork: succeeded")
                Result.success(data)
            }
            else -> {
                Log.d(TAG, "doWork: failed")
                Result.failure()
            }
        }
    }
}