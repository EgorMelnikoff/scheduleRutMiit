package com.egormelnikoff.schedulerutmiit.core.common.result

import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.SocketTimeoutException

sealed class TypedError {
    data class NetworkError(val exception: IOException) : TypedError()
    data class TimeoutError(val exception: SocketTimeoutException) : TypedError()
    data class HttpError(val code: Int, val message: String? = null) : TypedError()
    data class SerializationError(val exception: SerializationException) : TypedError()
    data class IllegalArgumentError(val exception: IllegalArgumentException) : TypedError()
    data class UnexpectedError(val exception: Throwable) : TypedError()
    data object EmptyBodyError : TypedError()
}

fun TypedError.isClientError(): Boolean =
    this is TypedError.HttpError && code in 400..499