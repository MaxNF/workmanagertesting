package com.example.work_manager_testing.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.WorkRequest
import androidx.work.testing.TestDriver
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun TestDriver.setAllConstraintsMet(workRequest: Collection<WorkRequest>) {
    workRequest.forEach {
        this.setAllConstraintsMet(it.id)
    }
}

fun TestDriver.setAllConstraintsMet(vararg workRequest: WorkRequest) {
    workRequest.forEach {
        this.setAllConstraintsMet(it.id)
    }
}

/** Модифицированный вариант. Оригинал тут: https://medium.com/androiddevelopers/unit-testing-livedata-and-other-common-observability-problems-bb477262eb04 */
fun <T> LiveData<T>.observeUntilOrTimeout(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    observeUntil: (T?) -> Boolean
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            if (observeUntil(o)) {
                latch.countDown()
                this@observeUntilOrTimeout.removeObserver(this)
            }
        }
    }
    this.observeForever(observer)

    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}