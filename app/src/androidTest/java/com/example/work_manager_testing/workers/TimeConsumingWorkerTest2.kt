package com.example.work_manager_testing.workers

import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.work_manager_testing.data.CallResult
import com.example.work_manager_testing.utils.TestWorkerFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.koin.test.KoinTest

internal class TimeConsumingWorkerTest2 : BaseWorkerTest(), KoinTest {

    private lateinit var worker: TimeConsumingWorker2

    @Before
    fun setUp() {
        val data = Data.Builder().putInt("key", 12345).build()
        worker = TestListenableWorkerBuilder<TimeConsumingWorker2>(context)
            .setWorkerFactory(TestWorkerFactory(repository))
            .setInputData(data)
            .build()
    }

    @Test
    fun givenTimeConsumingWorker2_whenDoWorkExecuted_resultIsSuccess() {
        repository.resultToReturn = CallResult.Success::class
        val result = runBlocking {
            worker.doWork()
        }
        assertThat(result).isInstanceOf(ListenableWorker.Result.Success::class.java)
    }

    @Test
    fun givenTimeConsumingWorker2_whenDoWorkExecuted_resultIsFailure() {
        repository.resultToReturn = CallResult.Error::class
        val result = runBlocking {
            worker.doWork()
        }
        assertThat(result).isInstanceOf(ListenableWorker.Result.Failure::class.java)
    }
}