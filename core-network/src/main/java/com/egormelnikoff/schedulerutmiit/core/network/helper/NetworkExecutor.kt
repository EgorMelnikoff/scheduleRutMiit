package com.egormelnikoff.schedulerutmiit.core.network.helper

import com.egormelnikoff.schedulerutmiit.core.common.logger.Logger
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.core.network.fetcher.ApiFetcher
import com.egormelnikoff.schedulerutmiit.core.network.fetcher.HtmlFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import org.jsoup.nodes.Document
import retrofit2.Response
import javax.inject.Inject

class NetworkExecutor @Inject constructor(
    private val logger: Logger,
    private val apiFetcher: ApiFetcher,
    private val htmlFetcher: HtmlFetcher
) {
    private suspend fun <T> execute(
        block: suspend () -> Result<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            block()
        } catch (e: SerializationException) {
            logger.e("NetworkHelper", "Serialization error", e)
            Result.Error(TypedError.SerializationError(e))
        } catch (e: IllegalArgumentException) {
            logger.e("NetworkHelper", "Illegal argument", e)
            Result.Error(TypedError.IllegalArgumentError(e))
        }
    }

    suspend fun <T : Any> callApi(
        retries: Int = 3,
        call: suspend () -> Response<T>
    ): Result<T> = execute {
        apiFetcher(retries, call)
    }

    suspend fun callHtml(
        retries: Int = 3, url: String
    ): Result<Document> = execute {
        htmlFetcher(retries, url)
    }
}