package com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser

import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.data.Error
import com.egormelnikoff.schedulerutmiit.data.Result
import org.jsoup.nodes.Document
import retrofit2.HttpException
import java.io.IOException

object ParserHelper {
    private const val BASE_URL = "https://www.miit.ru/people?"
    const val PEOPLE = "${BASE_URL}query="

    suspend fun callParserWithExceptions(
        logger: Logger,
        type: String,
        call: suspend () -> Document
    ): Result<Document> {
        return try {
            val document = call()
            logger.i("PARSER", "Success parsed data ($type)")
            Result.Success(
                data = document
            )
        } catch (e: HttpException) {
            logger.e("PARSER", "HttpException ($type)", e)
            Result.Error(
                Error.HttpError(
                    code = e.code(),
                    message = e.message(),
                )
            )
        } catch (e: IOException) {
            logger.e("PARSER", "IOException ($type)", e)
            Result.Error(Error.NetworkError(e))
        } catch (e: IllegalArgumentException) {
            logger.e("PARSER", "IllegalArgumentException ($type)", e)
            Result.Error(Error.IllegalArgumentError(e))
        }catch (e: Throwable) {
            logger.e("PARSER", "UnexpectedError ($type)", e)
            Result.Error(Error.UnexpectedError(e))
        }
    }
}