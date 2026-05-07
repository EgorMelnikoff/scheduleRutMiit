package com.egormelnikoff.schedulerutmiit.core.common.resources

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.EmptyBodyError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.HttpError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.IllegalArgumentError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.NetworkError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.SerializationError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.TimeoutError
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError.UnexpectedError
import javax.inject.Inject


class ResourcesManager @Inject constructor(
    private val context: Context
) {
    fun getString(id: Int) = context.getString(id)
    //fun getDrawable(id: Int) = context.getDrawable(id)
}

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

        is TimeoutError -> {
            resourcesManager.getString(
                R.string.timeout_expired
            )
        }

        is HttpError -> {
            "${
                resourcesManager.getString(
                    R.string.server_error
                )
            }: ${typedError.code}"
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
            typedError.exception.message?.let {
                "${resourcesManager.getString(R.string.error)}: $it}"
            } ?: resourcesManager.getString(R.string.unknown_error)
        }
    }
}