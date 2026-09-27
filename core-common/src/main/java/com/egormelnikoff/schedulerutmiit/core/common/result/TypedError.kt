package com.egormelnikoff.schedulerutmiit.core.common.result

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.EmptyBodyError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.HttpError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.IllegalArgumentError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.NetworkError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.SerializationError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.TimeoutError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.UnexpectedError
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface TypedError {
    data class NetworkError(val exception: IOException) : TypedError
    data class TimeoutError(val exception: SocketTimeoutException) : TypedError
    data class HttpError(val code: Int, val message: String? = null) : TypedError
    data class SerializationError(val exception: SerializationException) : TypedError
    data class IllegalArgumentError(val exception: IllegalArgumentException) : TypedError
    data class UnexpectedError(val exception: Throwable = Exception()) : TypedError
    data object EmptyBodyError : TypedError
}

fun TypedError.isRetryable(): Boolean =
    when (this) {
        is NetworkError -> true
        is TimeoutError -> true
        is HttpError -> code == 408 || code == 429 || code >= 500
        else -> false
    }

fun TypedError.getMessage(context: Context): String {
    return when (this) {
        is NetworkError -> context.getString(R.string.network_error)
        is TimeoutError -> context.getString(R.string.timeout_expired)
        is HttpError -> "${context.getString(R.string.server_error)}: ${this.code}"
        is SerializationError -> context.getString(R.string.serialization_error)
        is IllegalArgumentError -> context.getString(R.string.illegal_argument)
        is EmptyBodyError -> context.getString(R.string.empty_response)
        is UnexpectedError -> {
            this.exception.message?.let {
                "${context.getString(R.string.error)}: $it}"
            } ?: context.getString(R.string.unknown_error)
        }
    }
}