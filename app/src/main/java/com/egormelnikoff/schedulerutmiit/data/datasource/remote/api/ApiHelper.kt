package com.egormelnikoff.schedulerutmiit.data.datasource.remote.api

import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.NetworkLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class ApiHelper @Inject constructor(
    private val networkLogger: NetworkLogger
) {
    suspend fun <T : Any> callApiWithExceptions(
        requestType: String = "Unknown",
        requestParams: String? = null,
        retries: Int = 3,
        call: suspend () -> Response<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        repeat(retries) { attempt ->
            try {
                return@withContext executeCall(requestType, requestParams, call)
            } catch (e: IOException) {
                networkLogger.logError(
                    message = "IOException (${attempt + 1}/$retries)",
                    requestType = requestType,
                    requestParams = requestParams,
                    e = e
                )
                if (attempt == retries - 1) {
                    return@withContext Result.Error(TypedError.NetworkError(e))
                }
            } catch (e: TimeoutCancellationException) {
                networkLogger.logError(
                    message = "Timeout (${attempt + 1}/$retries)",
                    requestType = requestType,
                    requestParams = requestParams,
                    e = e
                )
                if (attempt == retries - 1) {
                    return@withContext Result.Error(TypedError.TimeoutError(e))
                }
            }
        }

        error("Unreachable")
    }

    private suspend fun <T : Any> executeCall(
        requestType: String,
        requestParams: String? = null,
        call: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = call()

            if (response.isSuccessful) {
                response.body()?.let {
                    networkLogger.logInfo(
                        message = "Success fetched data",
                        requestType = requestType,
                        requestParams = requestParams
                    )

                    Result.Success(it)
                } ?: run {
                    networkLogger.logInfo(
                        message = "Empty body",
                        requestType = requestType,
                        requestParams = requestParams
                    )
                    Result.Error(TypedError.EmptyBodyError)
                }
            } else {
                networkLogger.logError(
                    message = "Http error:\n${response.code()}, ${response.message()}",
                    requestType = requestType,
                    requestParams = requestParams
                )
                Result.Error(
                    TypedError.HttpError(
                        code = response.code(),
                        message = response.message()
                    )
                )
            }
        } catch (e: SerializationException) {
            networkLogger.logError(
                message = "SerializationException",
                requestType = requestType,
                requestParams = requestParams,
                e = e
            )
            Result.Error(TypedError.SerializationError(e))
        }
    }
}