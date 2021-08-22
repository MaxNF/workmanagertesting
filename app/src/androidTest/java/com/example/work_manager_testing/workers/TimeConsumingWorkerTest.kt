package com.example.work_manager_testing.workers

import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.work_manager_testing.data.CallResult
import com.example.work_manager_testing.utils.TestWorkerFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

/** Простой тест отдельно взятого воркера. На случай, если нам нужно изолированно протестировать логику. Ничего необычного */
internal class TimeConsumingWorkerTest : BaseWorkerTest() {

    private lateinit var worker: TimeConsumingWorker

    @Before
    fun setUp() {
        worker = TestListenableWorkerBuilder<TimeConsumingWorker>(context)
            // обязательно указываем кастомную тестовую фабрику, т.к. наши воркеры принимают в параметры конструктора репозиторий
            .setWorkerFactory(TestWorkerFactory(repository))
            .build()
    }

    @Test
    fun givenTimeConsumingWorker_whenDoWorkExecuted_resultIsSuccess() {
        // указываемый какой результат должен нам вернуть репозиторий. Можно использовать Мокито,
        // но я взял одно из уже готовых старых решений.
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