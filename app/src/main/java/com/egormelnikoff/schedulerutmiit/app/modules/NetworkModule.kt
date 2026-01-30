package com.egormelnikoff.schedulerutmiit.app.modules

import com.egormelnikoff.schedulerutmiit.data.datasource.remote.Endpoints.BASE_RUT_MIIT_URL
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
object MiitApiModule {
    @Provides
    @Singleton
    fun provideMiitApi(okHttpClient: OkHttpClient, gson: Gson): MiitApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_RUT_MIIT_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(MiitApi::class.java)
    }
}