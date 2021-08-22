package com.example.work_manager_testing

import com.example.work_manager_testing.domain.Repository
import com.example.work_manager_testing.fakerepository.FakeRepository
import com.example.work_manager_testing.workers.TimeConsumingWorker
import com.example.work_manager_testing.workers.TimeConsumingWorker2
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

/** В этом проекте в итоге не использовал тестовую Application, но задумка была подменять с помощью этого класса зависимости, которые
 * предоставляет Koin. Нужно будет подсовывать тестовую фабрику для воркеров, вместо настоящей и по идее все должно работать. */
class TestWorkerApp : WorkerApp() {
    override fun initKoin() {
        startKoin {
            androidContext(this@TestWorkerApp)
            androidLogger()
            workManagerFactory()
            modules(testAppModule)
        }
    }

    fun loadModule(module: Module) {
        loadKoinModules(module)
    }
}

val testAppModule = module {
    single<Repository> { FakeRepository() }
    viewModel { MainViewModel() }
    worker { TimeConsumingWorker(androidContext(), get(), get()) }
    worker { TimeConsumingWorker2(androidContext(), get(), get()) }
}