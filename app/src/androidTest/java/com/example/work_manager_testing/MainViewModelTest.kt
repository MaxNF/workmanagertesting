package com.example.work_manager_testing

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.WorkInfo
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.work_manager_testing.data.CallResult
import com.example.work_manager_testing.utils.TestWorkerFactory
import com.example.work_manager_testing.utils.observeUntilOrTimeout
import com.example.work_manager_testing.workers.BaseWorkerTest
import com.example.work_manager_testing.workers.TimeConsumingWorker
import com.example.work_manager_testing.workers.TimeConsumingWorker2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

/** Тестируем вью модел. По сути это тоже самое, что и WorkersChainTest. Смысла тестировать активити или фрагменты особо нет, т.к. там мы
 * просто мокнем содержимое нашей ViewModel и будем тестировать поведение интерфейса, без запуска настоящей логики Воркеров. Однако если
 * потребуется тест активити с настоящей вью моделью, то это возможно сделать. Однако для этого дополнительно нужно настроить di, чтобы
 * предоставлялись тестовые зависимости вместо настоящих. */
class MainViewModelTest : BaseWorkerTest() {

    private lateinit var viewModel: MainViewModel
    private val factory = TestWorkerFactory(repository)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        viewModel = MainViewModel()
        viewModel.createConstraints = { it ->
            it[TimeConsumingWorker::class] = Constraints.NONE
            it[TimeConsumingWorker2::class] = Constraints.NONE
        }

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
    fun givenViewModel_whenPlanWorkInvoked_thenWorkersFinishSuccessfully() {
        repository.resultToReturn = CallResult.Success::class
        viewModel.planWork(context)
        val liveData = viewModel.getWorkInfosLiveData(context)
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

    @Test
    fun givenViewModel_whenPlanWorkInvoked_thenWorkersFailed() {
        repository.resultToReturn = CallResult.Error::class
        viewModel.planWork(context)
        val liveData = viewModel.getWorkInfosLiveData(context)
        liveData.observeUntilOrTimeout(10, TimeUnit.SECONDS) { workInfos ->
            val anyFailed = workInfos?.any { it.state == WorkInfo.State.FAILED } ?: false
            val anyCancelled = workInfos?.any { it.state == WorkInfo.State.CANCELLED } ?: false
            val allSucceeded = workInfos?.all { it.state == WorkInfo.State.SUCCEEDED } ?: false
            anyFailed || anyCancelled || allSucceeded
        }

        assertTrue {
            liveData.value!!.all { it.state == WorkInfo.State.FAILED }
        }
    }
}