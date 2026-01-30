package com.egormelnikoff.schedulerutmiit.data.datasource.remote

import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import javax.inject.Inject

class NetworkLogger @Inject constructor(
    private val logger: Logger
) {
    fun logError(
        message: String,
        e: Throwable? = null,
        requestType: String,
        requestParams: String?
    ) {
        requestParams?.let { r -> logger.e(requestType,  "$r\n$message", e) }
            ?: logger.e(requestType, message, e)
    }

    fun logInfo(
        message: String,
        requestType: String,
        requestParams: String?
    ) {
        requestParams?.let { r -> logger.i(requestType, "$r\n$message") }
            ?: logger.i(requestType, message)
    }
}