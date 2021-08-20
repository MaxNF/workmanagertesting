package com.example.work_manager_testing

import android.app.Application
import com.example.work_manager_testing.data.RepositoryImpl
import com.example.work_manager_testing.domain.Repository
import com.example.work_manager_testing.workers.TimeConsumingWorker
import com.example.work_manager_testing.workers.TimeConsumingWorker2
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

open class WorkerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    open fun initKoin() {
        startKoin{
            androidContext(this@WorkerApp)
            androidLogger()
            workManagerFactory()
            modules(appModule)
        }
    }
}

val appModule = module {
    single<Repository> { RepositoryImpl(Dispatchers.IO) }
    viewModel { MainViewModel() }
    worker { TimeConsumingWorker(androidContext(), get(), get()) }
    worker { TimeConsumingWorker2(androidContext(), get(), get()) }
}