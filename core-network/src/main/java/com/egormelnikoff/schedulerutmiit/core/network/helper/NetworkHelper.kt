package com.egormelnikoff.schedulerutmiit.core.network.helper

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.core.network.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.SerializationException
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class NetworkHelper @Inject constructor(
    private val logger: Logger
) {
    private suspend fun <T> runWithRetry(
        requestType: String,
        requestParams: String? = null,
        retries: Int,
        timeoutMillis: Long,
        block: suspend () -> Result<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        repeat(retries) { attempt ->
            try {
                val result = withTimeout(timeoutMillis + 2000) {
                    block()
                }

                when (result) {
                    is Result.Success -> return@withContext result
                    is Result.Error -> {
                        val typedError = result.typedError
                        if (typedError is TypedError.HttpError && typedError.code >= 500) {
                            logError(
                                "Server error ${typedError.code}, retrying...",
                                requestType,
                                requestParams
                            )
                        } else {
                            return@withContext result
                        }

                    }
                }

            } catch (e: TimeoutCancellationException) {
                logError("Timeout (${attempt + 1}/$retries)", requestType, requestParams, e)

                if (attempt == retries - 1) {
                    return@withContext Result.Error(TypedError.TimeoutError(e))
                }
            } catch (e: IOException) {
                logError("Network error (${attempt + 1}/$retries)", requestType, requestParams, e)

                if (attempt == retries - 1) {
                    return@withContext Result.Error(TypedError.NetworkError(e))
                }

            }

            delay(1000L * (attempt + 1))

        }

        return@withContext Result.Error(TypedError.UnexpectedError(Exception("Retry failed")))
    }

    suspend fun <T : Any> callApi(
        requestType: String,
        requestParams: String? = null,
        retries: Int = 3,
        timeoutMs: Long = 20000,
        call: suspend () -> Response<T>
    ): Result<T> = runWithRetry(
        requestType = requestType,
        requestParams = requestParams,
        retries = retries,
        timeoutMillis = timeoutMs
    ) {
        try {
            val response = call()

            if (response.isSuccessful) {
                response.body()?.let {
                    logInfo("Success fetched data", requestType, requestParams)
                    Result.Success(it)
                } ?: Result.Error(TypedError.EmptyBodyError)
            } else {
                logError(
                    "Http error: ${response.code()} ${response.message()}",
                    requestType,
                    requestParams
                )
                Result.Error(TypedError.HttpError(response.code(), response.message()))
            }
        } catch (e: SerializationException) {
            logError("Serialization error", requestType, requestParams, e)
            Result.Error(TypedError.SerializationError(e))
        }
    }

    suspend fun callJsoup(
        requestType: String,
        requestParams: String? = null,
        retries: Int = 3,
        timeoutMs: Long = 20000,
        url: String
    ): Result<Document> = runWithRetry(
        requestType = requestType,
        requestParams = requestParams,
        retries = retries,
        timeoutMillis = timeoutMs
    ) {
        try {
            val result = Jsoup.connect(url).get()
            logInfo("Success parsed data", requestType, requestParams)
            Result.Success(result)
        } catch (e: HttpStatusException) {
            logError("Http error", requestType, requestParams, e)
            Result.Error(TypedError.HttpError(e.statusCode, e.message))
        } catch (e: IllegalArgumentException) {
            logError("Illegal argument", requestType, requestParams, e)
            Result.Error(TypedError.IllegalArgumentError(e))
        }
    }

    private fun logError(
        message: String,
        requestType: String,
        requestParams: String?,
        e: Throwable? = null,
    ) {
        requestParams?.let { r -> logger.e(requestType, "$r\n$message", e) }
            ?: logger.e(requestType, message, e)
    }

    private fun logInfo(
        message: String, requestType: String, requestParams: String?
    ) {
        requestParams?.let { r -> logger.i(requestType, "$r\n$message") } ?: logger.i(
            requestType,
            message
        )
    }
}