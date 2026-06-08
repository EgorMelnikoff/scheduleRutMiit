package com.egormelnikoff.schedulerutmiit.core.network.fetcher

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class HtmlFetcher @Inject constructor(
    private val retryExecutor: RetryExecutor,
    private val okHttpClient: OkHttpClient
) {
    suspend operator fun invoke(
        retries: Int,
        url: String
    ): Result<Document> {
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

                Result.Success(Jsoup.parse(body.string()))
            }
        }
    }
}