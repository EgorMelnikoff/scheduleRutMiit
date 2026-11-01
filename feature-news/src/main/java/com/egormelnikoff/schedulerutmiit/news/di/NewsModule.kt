package com.egormelnikoff.schedulerutmiit.news.di

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.news.data.remote.NewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NewsModule {
    @Provides
    @Singleton
    fun provideNewsApi(retrofitBuilder: Retrofit.Builder): NewsApi =
        retrofitBuilder
            .baseUrl(Endpoints.BASE_RUT_MIIT_URL)
            .build()
            .create(NewsApi::class.java)
}