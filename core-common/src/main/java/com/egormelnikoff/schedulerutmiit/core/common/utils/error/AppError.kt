package com.egormelnikoff.schedulerutmiit.core.common.utils.error

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.common.R
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface AppError {
    data class NetworkError(val exception: IOException) : AppError
    data class TimeoutError(val exception: SocketTimeoutException) : AppError
    data class HttpError(val code: Int, val message: String? = null) : AppError
    data class SerializationError(val exception: SerializationException) : AppError
    data class IllegalArgumentError(val exception: IllegalArgumentException) : AppError
    data class UnexpectedError(val exception: Throwable = Exception()) : AppError
    data object EmptyBodyError : AppError
}

fun Throwable.toAppError(): AppError = when (this) {
    is SocketTimeoutException -> AppError.TimeoutError(this)
    is HttpException -> AppError.HttpError(code = this.code(), message = this.message())
    is SerializationException -> AppError.SerializationError(this)
    is IllegalArgumentException -> AppError.IllegalArgumentError(this)
    is IOException -> {
        if (this.message?.contains("Empty body", ignoreCase = true) == true) {
            AppError.EmptyBodyError
        } else {
            AppError.NetworkError(this)
        }
    }

    else -> AppError.UnexpectedError(this)
}


fun AppError.isRetryable(): Boolean =
    when (this) {
        is AppError.NetworkError -> true
        is AppError.TimeoutError -> true
        is AppError.HttpError -> code == 408 || code == 429 || code >= 500
        else -> false
    }



fun AppError.getMessage(context: Context): String {
    return when (this) {
        is AppError.NetworkError -> context.getString(R.string.network_error)
        is AppError.TimeoutError -> context.getString(R.string.timeout_expired)
        is AppError.HttpError -> "${context.getString(R.string.server_error)}: ${this.code}"
        is AppError.SerializationError -> context.getString(R.string.serialization_error)
        is AppError.IllegalArgumentError -> context.getString(R.string.illegal_argument)
        is AppError.EmptyBodyError -> context.getString(R.string.empty_response)
        is AppError.UnexpectedError -> {
            this.exception.message?.let {
                "${context.getString(R.string.error)}: $it}"
            } ?: context.getString(R.string.unknown_error)
        }
    }
}