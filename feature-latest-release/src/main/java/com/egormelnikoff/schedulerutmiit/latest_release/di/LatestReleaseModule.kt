package com.egormelnikoff.schedulerutmiit.latest_release.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.latest_release.data.local.AppInfoProviderImpl
import com.egormelnikoff.schedulerutmiit.latest_release.data.local.LatestReleaseLocal
import com.egormelnikoff.schedulerutmiit.latest_release.data.remote.GithubApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LatestReleaseModule {
    @Provides
    @Singleton
    fun provideAppProvider(@ApplicationContext context: Context): AppInfoProviderImpl =
        AppInfoProviderImpl(context)

    @Provides
    @Singleton
    fun provideLatestReleaseLocal(
        @ApplicationContext context: Context,
        json: Json
    ): LatestReleaseLocal =
        LatestReleaseLocal(context, json)

    @Provides
    @Singleton
    fun provideGithubApi(retrofitBuilder: Retrofit.Builder): GithubApi =
        retrofitBuilder
            .baseUrl(Endpoints.API_GITHUB)
            .build()
            .create(GithubApi::class.java)
}