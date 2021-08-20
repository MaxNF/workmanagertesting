package com.example.work_manager_testing.data

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

sealed class CallResult<out T> {
    data class Success<out T>(val value: T) : CallResult<T>()
    object IOError : CallResult<Nothing>()
    object Error : CallResult<Nothing>()
    object FileAlreadyExistError : CallResult<Nothing>()
}

abstract class BaseRepository {
    abstract val TAG: String

    suspend fun <T> safeCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): CallResult<T> {
        return withContext(dispatcher) {
            try {
                CallResult.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                Log.e(TAG, "${throwable.printStackTrace()}")
                when (throwable) {
                    is FileAlreadyExistsException -> CallResult.FileAlreadyExistError
                    is IOException -> CallResult.IOError
                    else -> {
                        CallResult.Error
                    }
                }
            }
        }
    }
}