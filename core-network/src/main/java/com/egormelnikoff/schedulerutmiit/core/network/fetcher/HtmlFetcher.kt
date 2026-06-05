package com.egormelnikoff.schedulerutmiit.core.network.fetcher

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class HtmlFetcher @Inject constructor(
    private val retryExecutor: RetryExecutor,
    private val okHttpClient: OkHttpClient
) {

    suspend operator fun invoke(
        retries: Int,
        url: String
    ): Result<String> {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return retryExecutor.execute("HtmlFetcher", retries) {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@execute Result.Error(
                        TypedError.HttpError(
                            response.code,
                            response.message
                        )
                    )
                }

                val body = response.body
                    ?: return@execute Result.Error(TypedError.EmptyBodyError)

                Result.Success(body.string())
            }
        }
    }
}