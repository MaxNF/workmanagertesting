package com.example.work_manager_testing.fakerepository

import com.example.work_manager_testing.data.CallResult
import kotlin.reflect.KClass

abstract class BaseFakeRepository {
    var resultToReturn: KClass<out CallResult<*>>? = null
        get() {
            if (field == null) {
                throw UnsupportedOperationException("Define the class to return")
            } else {
                return field
            }
        }

    // Вместо этого можно использовать Мокито и устанавливать возвращаемый результат для конкретных методов для более точного тестирования.
    protected open fun <T> getResult(value: T): CallResult<T> {
        return when (resultToReturn) {
            CallResult.Success::class -> CallResult.Success(value)
            CallResult.IOError::class -> CallResult.IOError
            CallResult.FileAlreadyExistError::class -> CallResult.FileAlreadyExistError
            else -> CallResult.Error
        }
    }
}