package com.egormelnikoff.schedulerutmiit.core.network.fetcher

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import retrofit2.Response
import javax.inject.Inject

class ApiFetcher @Inject constructor(
    private val retryExecutor: RetryExecutor
) {
    suspend operator fun <T : Any> invoke(
        retries: Int,
        call: suspend () -> Response<T>
    ): Result<T> =
        retryExecutor.execute("ApiFetcher", retries) {
            val response = call()

            if (!response.isSuccessful) {
                return@execute Result.Error(
                    TypedError.HttpError(
                        response.code(),
                        response.message()
                    )
                )
            }

            val body = response.body()
                ?: return@execute Result.Error(TypedError.EmptyBodyError)

            Result.Success(body)
        }
}