package com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser

import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.Result
import org.jsoup.nodes.Document
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ParserHelper @Inject constructor(
    private val logger: Logger
) {
    companion object {
        private const val BASE_URL = "https://www.miit.ru/people?"
        const val PEOPLE = "${BASE_URL}query="
    }

    suspend fun callParserWithExceptions(
        fetchDataType: String,
        message: String,
        call: suspend () -> Document
    ): Result<Document> {
        return try {
            val document = call()
            logger.i("PARSER", "Success parsed data ($fetchDataType):\n$message")
            Result.Success(
                data = document
            )
        } catch (e: HttpException) {
            logger.e("PARSER", "Http error ($fetchDataType)", e)
            Result.Error(
                TypedError.HttpError(
                    code = e.code(),
                    message = e.message(),
                )
            )
        } catch (e: IOException) {
            logger.e("PARSER", "Network error ($fetchDataType)", e)
            Result.Error(TypedError.NetworkError(e))
        } catch (e: IllegalArgumentException) {
            logger.e("PARSER", "Illegal argument error ($fetchDataType)", e)
            Result.Error(TypedError.IllegalArgumentError(e))
        } catch (e: Throwable) {
            logger.e("PARSER", "Unexpected error ($fetchDataType)", e)
            Result.Error(TypedError.UnexpectedError(e))
        }
    }
}