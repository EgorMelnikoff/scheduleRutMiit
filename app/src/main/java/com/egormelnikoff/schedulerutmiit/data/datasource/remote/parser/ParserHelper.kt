package com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser

import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.NetworkLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.nodes.Document
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ParserHelper @Inject constructor(
    private val networkLogger: NetworkLogger
) {
    suspend fun callParserWithExceptions(
        requestType: String,
        requestParams: String,
        call: suspend () -> Document
    ): Result<Document> = withContext(Dispatchers.IO) {
        try {
            val document = call()
            networkLogger.logInfo(
                message = "Success parsed data",
                requestType = requestType,
                requestParams = requestParams
            )
            Result.Success(
                data = document
            )
        } catch (e: HttpException) {
            networkLogger.logError(
                message = "HttpException",
                requestType = requestType,
                requestParams = requestParams,
                e = e
            )
            Result.Error(
                TypedError.HttpError(
                    code = e.code(),
                    message = e.message(),
                )
            )
        } catch (e: IOException) {
            networkLogger.logError(
                message = "IOException",
                requestType = requestType,
                requestParams = requestParams,
                e = e
            )
            Result.Error(TypedError.NetworkError(e))
        } catch (e: IllegalArgumentException) {
            networkLogger.logError(
                message = "IllegalArgumentException",
                requestType = requestType,
                requestParams = requestParams,
                e = e
            )
            Result.Error(TypedError.IllegalArgumentError(e))
        }
    }
}