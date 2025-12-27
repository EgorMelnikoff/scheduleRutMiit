package com.egormelnikoff.schedulerutmiit.data.datasource.remote.api

import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class ApiHelper @Inject constructor(
    private val logger: Logger
) {
    companion object {
        const val BASE_GITHUB_URL = "https://api.github.com/"
        const val LATEST_RELEASE = "repos/EgorMelnikoff/scheduleRutMiit/releases/latest"

        const val BASE_MIIT_URL = "https://rut-miit.ru/"
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
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    message?.let { logger.i("API", "Success fetched data ($fetchDataType):\n$it") }
                        ?: logger.i("API", "Success fetched data ($fetchDataType)")
                    Result.Success(body)
                } else {
                    message?.let { logger.i("API", "Empty body ($fetchDataType):\n$it") }
                        ?: logger.i("API", "Empty body ($fetchDataType)")
                    Result.Error(TypedError.EmptyBodyError)
                }
            } else {
                message?.let { logger.i("API", "Http error ($fetchDataType):\n$it\n${response.code()}, ${response.message()}") }
                    ?: logger.i("API", "Http error ($fetchDataType):\n${response.code()}, ${response.message()}")
                Result.Error(
                    TypedError.HttpError(
                        code = response.code(),
                        message = response.message()
                    )
                )
            }
        } catch (e: IOException) {
            message?.let { logger.e("API", "IOException ($fetchDataType):\n$it", e) }
                ?: logger.e("API", "IOException ($fetchDataType)", e)
            Result.Error(TypedError.NetworkError(e))
        } catch (e: SerializationException) {
            message?.let { logger.e("API", "Serialization error ($fetchDataType):\n$it", e) }
                ?: logger.e("API", "Serialization error ($fetchDataType)", e)
            Result.Error(TypedError.SerializationError(e))
        } catch (e: Throwable) {
            message?.let { logger.e("API", "Unexpected error ($fetchDataType):\n$it", e) }
                ?: logger.e("API", "Unexpected error ($fetchDataType)", e)
            Result.Error(TypedError.UnexpectedError(e))
        }
    }
}