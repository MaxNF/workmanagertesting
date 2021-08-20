package com.example.work_manager_testing.domain

import com.example.work_manager_testing.data.CallResult

interface Repository {
    suspend fun performLongRunningTaskAndGetRandomInt(): CallResult<Int>
    suspend fun performCalculationTask(i: Int): CallResult<Int>
}