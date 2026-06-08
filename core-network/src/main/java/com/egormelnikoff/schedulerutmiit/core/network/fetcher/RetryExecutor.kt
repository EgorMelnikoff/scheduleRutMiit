package com.egormelnikoff.schedulerutmiit.core.network.fetcher

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.core.common.result.isRetryable
import com.egormelnikoff.schedulerutmiit.core.network.logger.Logger
import kotlinx.coroutines.delay
import okio.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

class RetryExecutor @Inject constructor(
    private val logger: Logger
) {
    suspend fun <T> execute(
        tag: String,
        retries: Int,
        block: suspend () -> Result<T>
    ): Result<T> {
        repeat(retries) { attempt ->
            try {
                when (val result = block()) {
                    is Result.Success -> return result
                    is Result.Error -> {
                        if (attempt == retries - 1) {
                            return result
                        }

                        if (result.typedError.isRetryable()) {
                            return result
                        }
                    }
                }
            } catch (e: SocketTimeoutException) {
                logger.e(tag, "Timeout (${attempt + 1}/$retries)", e)

                if (attempt == retries - 1) {
                    return Result.Error(TypedError.TimeoutError(e))
                }
            } catch (e: IOException) {
                logger.e(tag, "Network error (${attempt + 1}/$retries)", e)

                if (attempt == retries - 1) {
                    return Result.Error(TypedError.NetworkError(e))
                }
            }

            delay(backoff(attempt).milliseconds)
        }

        return Result.Error(
            TypedError.UnexpectedError(
                Exception("Retry failed")
            )
        )
    }

    private fun backoff(attempt: Int): Long {
        return (500L * (2.0.pow(attempt.toDouble()))).toLong()
    }
}