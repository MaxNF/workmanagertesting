package com.example.work_manager_testing.fakerepository

import com.example.work_manager_testing.data.CallResult
import com.example.work_manager_testing.domain.Repository
import kotlinx.coroutines.delay
import kotlin.random.Random

class FakeRepository : BaseFakeRepository(), Repository {
    override suspend fun performLongRunningTaskAndGetRandomInt(): CallResult<Int> {
        delay(1000)
        return getResult(Random.nextInt())
    }

    override suspend fun performCalculationTask(i: Int): CallResult<Int> {
        delay(500)
        return getResult(i * 2)
    }
}