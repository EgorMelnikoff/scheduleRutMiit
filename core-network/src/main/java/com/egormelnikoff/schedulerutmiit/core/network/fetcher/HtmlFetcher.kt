package com.egormelnikoff.schedulerutmiit.core.network.fetcher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import javax.inject.Inject

class HtmlFetcher @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    suspend operator fun invoke(
        url: String
    ): Document = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val bodyString = okHttpClient.newCall(request).executeAndGetBody()
        return@withContext Jsoup.parse(bodyString)
    }

    private fun okhttp3.Call.executeAndGetBody(): String {
        this.execute().use { response ->
            if (!response.isSuccessful) {
                throw HttpStatusException(
                    "HTTP Error",
                    response.code,
                    response.request.url.toString()
                )
            }

            return response.body?.string() ?: throw IOException("Empty body")
        }
    }
}