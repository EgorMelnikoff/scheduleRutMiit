package com.egormelnikoff.schedulerutmiit.core.ui.event

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