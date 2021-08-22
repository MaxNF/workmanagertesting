package com.example.work_manager_testing.utils

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.example.work_manager_testing.TestWorkerApp

/** Необходимо для подмены настоящей Application на тестовую. Тут не стал использовать. */
class KoinTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestWorkerApp::class.java.name, context)
    }
}