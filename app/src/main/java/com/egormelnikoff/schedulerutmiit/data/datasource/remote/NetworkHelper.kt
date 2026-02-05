package com.egormelnikoff.schedulerutmiit.data.datasource.remote

import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.SerializationException
import org.jsoup.HttpStatusException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class NetworkHelper @Inject constructor(
    private val logger: Logger
) {
    suspend fun <T : Any> callNetwork(
        requestType: String = "Unknown",
        requestParams: String? = null,
        retries: Int = 3,
        timeoutMs: Long = 20000,
        callApi: (suspend () -> Response<T>)?,
        callParser: (suspend () -> T)?
    ): Result<T> = withContext(Dispatchers.IO) {
        repeat(retries) { attempt ->
            try {
                val result = withTimeout(timeoutMs) {
                    callApi?.let {
                        executeApiCall(requestType, requestParams, it)
                    } ?: callParser?.let {
                        executeParserCall(requestType, requestParams, it)
                    }
                }

                result?.let {
                    return@withContext result
                }
            } catch (e: IOException) {
                logError(
                    message = "Network error (${attempt + 1}/$retries)",
                    requestType = requestType,
                    requestParams = requestParams,
                    e = e
                )
                if (attempt == retries - 1) {
                    return@withContext Result.Error(TypedError.NetworkError(e))
                }
                delay(1000)
            } catch (e: TimeoutCancellationException) {
                logError(
                    message = "Timeout error (${attempt + 1}/$retries)",
                    requestType = requestType,
                    requestParams = requestParams,
                    e = e
                )
                if (attempt == retries - 1) {
                    return@withContext Result.Error(TypedError.TimeoutError(e))
                }
                delay(1000)
            }
        }
        return@withContext Result.Error(TypedError.UnexpectedError(Exception("Unknown error")))
    }

    private suspend fun <T : Any> executeApiCall(
        requestType: String, requestParams: String? = null, call: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = call()

            if (response.isSuccessful) {
                response.body()?.let {
                    logInfo(
                        message = "Success fetched data",
                        requestType = requestType,
                        requestParams = requestParams
                    )

                    Result.Success(it)
                } ?: run {
                    logInfo(
                        message = "Empty body",
                        requestType = requestType,
                        requestParams = requestParams
                    )
                    Result.Error(TypedError.EmptyBodyError)
                }
            } else {
                logError(
                    message = "Http error: ${response.code()}, ${response.message()}",
                    requestType = requestType,
                    requestParams = requestParams
                )
                Result.Error(
                    TypedError.HttpError(
                        code = response.code(), message = response.message()
                    )
                )
            }
        } catch (e: SerializationException) {
            logError(
                message = "Serialization error",
                requestType = requestType,
                requestParams = requestParams,
                e = e
            )
            Result.Error(TypedError.SerializationError(e))
        }
    }

    private suspend fun <T : Any> executeParserCall(
        requestType: String, requestParams: String? = null, call: suspend () -> T
    ): Result<T> {
        return try {
            val document = call()
            logInfo(
                message = "Success parsed data",
                requestType = requestType,
                requestParams = requestParams
            )
            Result.Success(
                data = document
            )
        } catch (e: HttpStatusException) {
            logError(
                message = "Http error",
                requestType = requestType,
                requestParams = requestParams,
                e = e
            )
            return Result.Error(
                TypedError.HttpError(
                    code = e.statusCode,
                    message = e.message,
                )
            )
        } catch (e: IllegalArgumentException) {
            logError(
                message = "Illegal argument error",
                requestType = requestType,
                requestParams = requestParams,
                e = e
            )
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