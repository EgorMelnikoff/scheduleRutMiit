package com.egormelnikoff.schedulerutmiit.data.datasource.remote.api

import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.data.Error
import com.egormelnikoff.schedulerutmiit.data.Result
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class MiitApiHelper @Inject constructor(
    private val logger: Logger
) {
    companion object {
        const val BASE_URL = "https://rut-miit.ru/"
        private const val DATA = "data-service/data/"

        const val GROUPS = "${DATA}timetable/groups-catalog"
        const val TIMETABLE = "${DATA}timetable/v2/{type}/{apiId}"
        const val SCHEDULE = "${DATA}timetable/v2/{type}/{apiId}/{timetableId}"

        const val NEWS_CATALOG = "${DATA}news?idk_information_category=2"
        const val NEWS = "${DATA}news/{newsId}"
    }

    suspend fun <T : Any> callApiWithExceptions(
        fetchDataType: String,
        message: String,
        call: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    logger.i("API", "Success fetched data ($fetchDataType):\n$message")
                    Result.Success(body)
                } else {
                    logger.i("API", "Empty body ($fetchDataType)")
                    Result.Error(Error.EmptyBodyError)
                }
            } else {
                logger.i("API", "Http error ($fetchDataType)")
                Result.Error(
                    Error.HttpError(
                        code = response.code(),
                        message = response.message()
                    )
                )
            }
        } catch (e: HttpException) {
            logger.e("API", "Http error ($fetchDataType)", e)
            Result.Error(
                Error.HttpError(
                    code = e.code(),
                    message = e.message(),
                )
            )
        } catch (e: IOException) {
            logger.e("API", "IOException ($fetchDataType)", e)
            Result.Error(Error.NetworkError(e))
        } catch (e: SerializationException) {
            logger.e("API", "Serialization error ($fetchDataType)", e)
            Result.Error(Error.SerializationError(e))
        } catch (e: Throwable) {
            logger.e("API", "Unexpected error ($fetchDataType)", e)
            Result.Error(Error.UnexpectedError(e))
        }
    }
}