package com.egormelnikoff.schedulerutmiit.data.repos.remote.api

import android.util.Log
import com.egormelnikoff.schedulerutmiit.data.repos.Result
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Api {
    companion object {
        private val gson = GsonBuilder()
            .registerTypeAdapter(
                LocalDate::class.java,
                JsonDeserializer { json, _, _ ->
                    LocalDate.parse(json.asString, DateTimeFormatter.ISO_DATE)
                }
            )
            .registerTypeAdapter(
                LocalDateTime::class.java,
                JsonDeserializer { json, _, _ ->
                    LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_ZONED_DATE_TIME)

                }
            ).create()

        suspend fun getData(url: URL): String? {
            return withContext(Dispatchers.IO) {
                var connection: HttpURLConnection? = null
                try {
                    connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 3000
                    connection.readTimeout = 20000

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        connection.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        Log.e(
                            "NetworkError",
                            "Error response code: ${connection.responseCode}"
                        )
                        null
                    }
                } catch (e: Exception) {
                    Log.e("NetworkError", "Exception while fetching $url: ${e.message}")
                    null
                } finally {
                    connection?.disconnect()
                }
            }
        }

        fun <T> parseJson(jsonString: String?, classOfT: Class<T>): Result<T> {
            return try {
                println(jsonString)
                if (!jsonString.isNullOrEmpty()) {
                    Result.Success(gson.fromJson(jsonString, classOfT))
                } else {
                    Result.Error(Exception("Empty JSON"))
                }
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                Result.Error(e)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Error(e)
            }
        }
    }
}