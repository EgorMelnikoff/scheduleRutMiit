package com.egormelnikoff.schedulerutmiit.di

import com.egormelnikoff.schedulerutmiit.data.remote.api.GithubApi
import com.egormelnikoff.schedulerutmiit.data.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.remote.network.Endpoints.API_GITHUB
import com.egormelnikoff.schedulerutmiit.data.remote.network.Endpoints.BASE_RUT_MIIT_URL
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36"
                    )
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object MiitModule {
    @Provides
    @Singleton
    fun provideMiitApi(okHttpClient: OkHttpClient, json: Json): MiitApi {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(BASE_RUT_MIIT_URL)
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
            .baseUrl(API_GITHUB)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(GithubApi::class.java)
    }
}