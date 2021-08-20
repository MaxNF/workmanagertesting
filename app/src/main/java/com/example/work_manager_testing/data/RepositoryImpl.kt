package com.example.work_manager_testing.data

import com.example.work_manager_testing.domain.Repository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlin.random.Random

class RepositoryImpl(private val dispatcher: CoroutineDispatcher) : BaseRepository(), Repository {
    override val TAG: String = "Repository"

    override suspend fun performLongRunningTaskAndGetRandomInt() = safeCall(dispatcher) {
        delay(1000)
        Random.nextInt()
    }

    override suspend fun performCalculationTask(i: Int): CallResult<Int> = safeCall(dispatcher) {
        delay(500)
        i * 2
    }
}