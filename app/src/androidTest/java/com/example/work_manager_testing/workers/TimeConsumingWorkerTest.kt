package com.example.work_manager_testing.workers

import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.work_manager_testing.data.CallResult
import com.example.work_manager_testing.utils.TestWorkerFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.koin.test.KoinTest

internal class TimeConsumingWorkerTest : BaseWorkerTest(), KoinTest {

    private lateinit var worker: TimeConsumingWorker

    @Before
    fun setUp() {
        worker = TestListenableWorkerBuilder<TimeConsumingWorker>(context)
            .setWorkerFactory(TestWorkerFactory(repository))
            .build()
    }

    @Test
    fun givenTimeConsumingWorker_whenDoWorkExecuted_resultIsSuccess() {
        repository.resultToReturn = CallResult.Success::class
        val result = runBlocking {
            worker.doWork()
        }
        assertThat(result).isInstanceOf(ListenableWorker.Result.Success::class.java)
    }

    @Test
    fun givenTimeConsumingWorker_whenDoWorkExecuted_resultIsFailure() {
        repository.resultToReturn = CallResult.Error::class
        val result = runBlocking {
            worker.doWork()
        }
        assertThat(result).isInstanceOf(ListenableWorker.Result.Failure::class.java)
    }
}