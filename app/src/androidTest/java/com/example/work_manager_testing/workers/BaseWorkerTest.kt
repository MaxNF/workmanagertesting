package com.example.work_manager_testing.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.work_manager_testing.fakerepository.FakeRepository
import kotlinx.coroutines.test.TestCoroutineDispatcher

/** Базовый класс для теста воркеров, чтобы не дублировать код. */
open class BaseWorkerTest {
    protected var context: Context = ApplicationProvider.getApplicationContext()
    protected var repository: FakeRepository = FakeRepository()
    protected val testDispatcher = TestCoroutineDispatcher()
}