package com.egormelnikoff.schedulerutmiit.data.datasource.remote.api

import com.egormelnikoff.schedulerutmiit.data.Result
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSyntaxException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface Api {
    suspend fun getData(url: URL): Result<String>
    fun <T> parseJson(jsonString: Result<String?>, classOfT: Class<T>): Result<T>
}

class ApiImpl : Api {
    private val httpClient = HttpClient(CIO) {
        engine {
            requestTimeout = 25000
        }
    }

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


    override suspend fun getData(url: URL): Result<String> {
        println(url)
        return try {
            val response = httpClient.get(url)

            if (response.status == HttpStatusCode.OK) {
                Result.Success(response.body<String>())
            } else {
                Result.Error(
                    Exception("Error response code: ${response.status}")
                )
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun <T> parseJson(jsonString: Result<String?>, classOfT: Class<T>): Result<T> {
        return try {
            when (jsonString) {
                is Result.Success -> {
                    if (!jsonString.data.isNullOrEmpty()) {
                        Result.Success(gson.fromJson(jsonString.data, classOfT))
                    } else {
                        Result.Error(Exception("Empty JSON"))
                    }
                }

                is Result.Error -> {
                    Result.Error(jsonString.exception)
                }
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