package com.egormelnikoff.schedulerutmiit.data.datasource.remote.api

import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
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
        message: String? = null,
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
                    Result.Error(TypedError.EmptyBodyError)
                }
            } else {
                logger.i("API", "Http error ($fetchDataType):\n$message\n${response.code()}, ${response.message()}")
                Result.Error(
                    TypedError.HttpError(
                        code = response.code(),
                        message = response.message()
                    )
                )
            }
        } catch (e: HttpException) {
            logger.e("API", "Http error ($fetchDataType):\n$message", e)
            Result.Error(
                TypedError.HttpError(
                    code = e.code(),
                    message = e.message(),
                )
            )
        } catch (e: IOException) {
            logger.e("API", "IOException ($fetchDataType):\n$message", e)
            Result.Error(TypedError.NetworkError(e))
        } catch (e: SerializationException) {
            logger.e("API", "Serialization error ($fetchDataType):\n$message", e)
            Result.Error(TypedError.SerializationError(e))
        } catch (e: Throwable) {
            logger.e("API", "Unexpected error ($fetchDataType):\n$message", e)
            Result.Error(TypedError.UnexpectedError(e))
        }
    }
}