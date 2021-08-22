package com.example.work_manager_testing.workers

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.work_manager_testing.MainViewModel
import com.example.work_manager_testing.data.CallResult
import com.example.work_manager_testing.utils.TestWorkerFactory
import com.example.work_manager_testing.utils.observeUntilOrTimeout
import com.example.work_manager_testing.utils.setAllConstraintsMet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

private const val TAG = "WorkerTest"

class WorkersChainTest : BaseWorkerTest() {

    private val factory = TestWorkerFactory(repository)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val executor = SynchronousExecutor()
        val config = Configuration.Builder()
            .setWorkerFactory(factory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(executor)
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    @Test
    fun givenWorkersChain_whenExecuted_thenResultSuccess() {
        Log.d(TAG, "givenWorkersChain_whenExecuted_thenResultSuccess: Thread = ${Thread.currentThread().name}")
        repository.resultToReturn = CallResult.Success::class
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

        val workManager = WorkManager.getInstance(context).also {
            it.beginUniqueWork(MainViewModel.WORK_UNIQUE_NAME, ExistingWorkPolicy.KEEP, workRequestFirst)
                .then(workRequestSecond)
                .enqueue()
                .result
                .get()
        }

        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!
        testDriver.setAllConstraintsMet(workRequestFirst)

        val liveData = workManager.getWorkInfosForUniqueWorkLiveData(MainViewModel.WORK_UNIQUE_NAME)

        // Ждем пока лямбда не вернет true, либо не выйдет таймаут. В будущем можно сделать более удобные и краткие экстеншен функции
        // специально под WorkInfo.State. Определять какой именно воркер сломался можно вытаскивая id либо тэг у WorkInfo.
        liveData.observeUntilOrTimeout(10, TimeUnit.SECONDS) { workInfos ->
            val anyFailed = workInfos?.any { it.state == WorkInfo.State.FAILED } ?: false
            val anyCancelled = workInfos?.any { it.state == WorkInfo.State.CANCELLED } ?: false
            val allSucceeded = workInfos?.all { it.state == WorkInfo.State.SUCCEEDED } ?: false
            anyFailed || anyCancelled || allSucceeded
        }

        assertTrue {
            liveData.value!!.all { it.state == WorkInfo.State.SUCCEEDED }
        }
    }
}