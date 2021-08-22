package com.example.work_manager_testing

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.example.work_manager_testing.workers.TimeConsumingWorker
import com.example.work_manager_testing.workers.TimeConsumingWorker2
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class MainViewModel : ViewModel() {

    companion object {
        const val WORK_UNIQUE_NAME = "work"
    }

    private val constraints = mutableMapOf<KClass<out CoroutineWorker>, Constraints>()

    // Задумка в следующем: в тестах мы поместим сюда логику создания Constraints для тестов (то есть Constraints.NONE). Почему это важно:
    // у TestDriver есть метод testDriver.setAllConstraintsMet(WorkRequestId), который симулирует выполнения всех ограничений для конкретного воркера.
    // Однако я не нашел способ симулировать ограничения для еще не помещенного в очередь вокера, который будет выполнен по цепочке (боюсь, что это не предусмотрели).
    // Поэтому как вариант - просто убирать все ограничения в тестах.
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var createConstraints: (c: MutableMap<KClass<out CoroutineWorker>, Constraints>) -> Unit = {
        val commonConstraints = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        it[TimeConsumingWorker::class] = commonConstraints
        it[TimeConsumingWorker2::class] = commonConstraints
    }

    fun getWorkInfosLiveData(context: Context) = WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(WORK_UNIQUE_NAME)

    fun planWork(context: Context) {
        createConstraints(constraints)
        val workRequestFirst = OneTimeWorkRequestBuilder<TimeConsumingWorker>()
            .setConstraints(constraints[TimeConsumingWorker::class]!!)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        val workRequestSecond = OneTimeWorkRequestBuilder<TimeConsumingWorker2>()
            .setConstraints(constraints[TimeConsumingWorker2::class]!!)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .beginUniqueWork(WORK_UNIQUE_NAME, ExistingWorkPolicy.KEEP, workRequestFirst)
            .then(workRequestSecond)
            .enqueue()
    }
}