package com.egormelnikoff.schedulerutmiit.core.settings.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.core.settings.data.local.AppInfoProvider
import com.egormelnikoff.schedulerutmiit.core.settings.data.local.SettingsLocal
import com.egormelnikoff.schedulerutmiit.core.settings.data.remote.GithubApi
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
internal object SettingsModule {
    @Provides
    @Singleton
    fun provideSettingsLocal(
        @ApplicationContext context: Context,
        json: Json
    ): SettingsLocal = SettingsLocal(
        context = context,
        json = json
    )

    @Provides
    @Singleton
    fun provideAppProvider(@ApplicationContext context: Context): AppInfoProvider =
        AppInfoProvider(context)

    @Provides
    @Singleton
    fun provideGithubApi(retrofitBuilder: Retrofit.Builder): GithubApi =
        retrofitBuilder
            .baseUrl(Endpoints.API_GITHUB)
            .build()
            .create(GithubApi::class.java)
}