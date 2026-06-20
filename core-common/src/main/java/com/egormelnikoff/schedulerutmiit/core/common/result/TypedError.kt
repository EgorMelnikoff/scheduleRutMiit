package com.egormelnikoff.schedulerutmiit.core.common.result

import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface TypedError {
    data class NetworkError(val exception: IOException) : TypedError
    data class TimeoutError(val exception: SocketTimeoutException) : TypedError
    data class HttpError(val code: Int, val message: String? = null) : TypedError
    data class SerializationError(val exception: SerializationException) : TypedError
    data class IllegalArgumentError(val exception: IllegalArgumentException) : TypedError
    data class UnexpectedError(val exception: Throwable) : TypedError
    data object EmptyBodyError : TypedError
}

fun TypedError.isRetryable(): Boolean =
    when (this) {
        is TypedError.NetworkError -> true
        is TypedError.TimeoutError -> true
        is TypedError.HttpError -> code == 408 || code == 429 || code >= 500
        else -> false
    }