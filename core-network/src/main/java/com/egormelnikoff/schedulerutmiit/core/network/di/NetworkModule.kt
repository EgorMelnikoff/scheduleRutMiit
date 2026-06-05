package com.egormelnikoff.schedulerutmiit.core.network.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.network.api.GithubApi
import com.egormelnikoff.schedulerutmiit.core.network.api.MiitApi
import com.egormelnikoff.schedulerutmiit.core.network.endpoins.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.logger.Logger
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36"

    @Provides
    @Singleton
    fun provideOkHttpClient(logger: Logger): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("User-Agent", USER_AGENT)
                    .build()

                chain.proceed(newRequest).let { response ->
                    if (!response.isSuccessful) {
                        logger.e(
                            "NetworkInterceptor",
                            "Server error (${response.code}): ${newRequest.url}"
                        )
                    }
                    response
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideLogger(
        @ApplicationContext context: Context
    ): Logger = Logger(context)
}

@Module
@InstallIn(SingletonComponent::class)
object MiitModule {
    @Provides
    @Singleton
    fun provideMiitApi(okHttpClient: OkHttpClient, json: Json): MiitApi {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(Endpoints.BASE_RUT_MIIT_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(MiitApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object GithubModule {
    @Provides
    @Singleton
    fun provideGithubApi(okHttpClient: OkHttpClient, json: Json): GithubApi {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(Endpoints.API_GITHUB)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(GithubApi::class.java)
    }
}