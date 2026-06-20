package com.egormelnikoff.schedulerutmiit.core.network.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.common.logger.Logger
import com.egormelnikoff.schedulerutmiit.core.network.api.GithubApi
import com.egormelnikoff.schedulerutmiit.core.network.api.MiitApi
import com.egormelnikoff.schedulerutmiit.core.network.endpoins.Endpoints
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36"

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        val cache = Cache(
            directory = File(context.cacheDir, "http_cache"),
            maxSize = 100L * 1024 * 1024
        )

        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .cache(cache)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", USER_AGENT)
                    .build()

                chain.proceed(request)
            }
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=21600")
                    .build()
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideLogger(
        @ApplicationContext context: Context
    ): Logger = Logger(context)

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        okHttpClient: OkHttpClient, json: Json
    ): Retrofit.Builder {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
    }

    @Provides
    @Singleton
    fun provideMiitApi(retrofitBuilder: Retrofit.Builder): MiitApi =
        retrofitBuilder
            .baseUrl(Endpoints.BASE_RUT_MIIT_URL)
            .build()
            .create(MiitApi::class.java)

    @Provides
    @Singleton
    fun provideGithubApi(retrofitBuilder: Retrofit.Builder): GithubApi =
        retrofitBuilder
            .baseUrl(Endpoints.API_GITHUB)
            .build()
            .create(GithubApi::class.java)

}