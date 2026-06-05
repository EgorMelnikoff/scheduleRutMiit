package com.egormelnikoff.schedulerutmiit.core.network.helper

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.core.network.fetcher.ApiFetcher
import com.egormelnikoff.schedulerutmiit.core.network.fetcher.HtmlFetcher
import com.egormelnikoff.schedulerutmiit.core.network.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Response
import javax.inject.Inject

class NetworkHelper @Inject constructor(
    private val logger: Logger,
    private val apiFetcher: ApiFetcher,
    private val htmlFetcher: HtmlFetcher
) {
    suspend fun <T : Any> callApi(
        retries: Int = 3,
        call: suspend () -> Response<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        return@withContext try {
            apiFetcher(retries, call)
        } catch (e: SerializationException) {
            logger.e("NetworkHelper", "Serialization error", e)
            Result.Error(TypedError.SerializationError(e))
        } catch (e: IllegalArgumentException) {
            logger.e("NetworkHelper", "Illegal argument", e)
            Result.Error(TypedError.IllegalArgumentError(e))
        }
    }

    suspend fun callHtml(
        retries: Int = 3,
        url: String
    ): Result<Document> = withContext(Dispatchers.IO) {
        return@withContext try {
            htmlFetcher(retries, url).let {
                when (it) {
                    is Result.Success -> Result.Success(Jsoup.parse(it.data))
                    is Result.Error -> it
                }
            }
        } catch (e: IllegalArgumentException) {
            logger.e("NetworkHelper", "Illegal argument", e)
            Result.Error(TypedError.IllegalArgumentError(e))
        }
    }
}