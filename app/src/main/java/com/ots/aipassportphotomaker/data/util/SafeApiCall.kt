package com.ots.aipassportphotomaker.data.util

import com.ots.aipassportphotomaker.domain.util.Result

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> = try {
    Result.Success(apiCall.invoke())
} catch (e: Exception) {
    Result.Error(e)
}
