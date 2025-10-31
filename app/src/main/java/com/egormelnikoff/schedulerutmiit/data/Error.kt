package com.egormelnikoff.schedulerutmiit.data

import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import okio.IOException

sealed class Error {
    data class NetworkError(val exception: IOException) : Error()
    data class HttpError(val code: Int, val message: String? = null) : Error()
    data class SerializationError(val exception: Exception) : Error()
    data class IllegalArgumentError(val exception: Exception) : Error()
    data class UnexpectedError(val exception: Throwable) : Error()
    object EmptyBodyError : Error()

    companion object {
        fun getErrorMessage(
            resourcesManager: ResourcesManager,
            data: Error
        ): String? {
            return when (data) {
                is NetworkError -> {
                    resourcesManager.getString(
                        R.string.network_error
                    )
                }
                is HttpError -> {
                    "${resourcesManager.getString(
                        R.string.server_error
                    )}: ${data.code}"
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