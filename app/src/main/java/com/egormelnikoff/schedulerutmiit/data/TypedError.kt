package com.egormelnikoff.schedulerutmiit.data

import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.SerializationException
import okio.IOException

sealed class TypedError {
    data class NetworkError(val exception: IOException) : TypedError()
    data class TimeoutError(val exception: TimeoutCancellationException) : TypedError()
    data class HttpError(val code: Int, val message: String? = null) : TypedError()
    data class SerializationError(val exception: SerializationException) : TypedError()
    data class IllegalArgumentError(val exception: IllegalArgumentException) : TypedError()
    data class UnexpectedError(val exception: Throwable) : TypedError()
    data object EmptyBodyError : TypedError()

    companion object {
        fun getErrorMessage(
            resourcesManager: ResourcesManager,
            typedError: TypedError
        ): String? {
            return when (typedError) {
                is NetworkError -> {
                    resourcesManager.getString(
                        R.string.network_error
                    )
                }
                is TimeoutError-> {
                    resourcesManager.getString(
                        R.string.timeout_expired
                    )
                }
                is HttpError -> {
                    "${resourcesManager.getString(
                        R.string.server_error
                    )}: ${typedError.code}"
                }
                is SerializationError -> {
                    resourcesManager.getString(
                        R.string.serialization_error
                    )
                }
                is IllegalArgumentError -> {
                    resourcesManager.getString(
                        R.string.illegal_argument
                    )
                }
                is EmptyBodyError -> {
                    resourcesManager.getString(
                        R.string.empty_response
                    )
                }
                is UnexpectedError -> {
                    resourcesManager.getString(
                        R.string.unknown_error
                    )
                }
            }
        }
    }
}