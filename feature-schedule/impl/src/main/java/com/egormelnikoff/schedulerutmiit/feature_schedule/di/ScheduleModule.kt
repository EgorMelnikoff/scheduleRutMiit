package com.egormelnikoff.schedulerutmiit.feature_schedule.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.NamedScheduleDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.ScheduleApi
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.file.FileExporter
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.file.FileImporter
import com.egormelnikoff.schedulerutmiit.feature_schedule.glance.model.WidgetDataUpdaterImpl
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
object ScheduleModule {
    @Provides
    @Singleton
    fun provideFileExporter(@ApplicationContext context: Context) = FileExporter(context)

    @Provides
    @Singleton
    fun provideFileImporter(@ApplicationContext context: Context) = FileImporter(context)

    @Provides
    @Singleton
    fun provideScheduleApi(retrofitBuilder: Retrofit.Builder): ScheduleApi =
        retrofitBuilder
            .baseUrl(Endpoints.BASE_RUT_MIIT_URL)
            .build()
            .create(ScheduleApi::class.java)

    @Provides
    @Singleton
    fun provideWidgetUpdater(
        @ApplicationContext context: Context,
        namedScheduleDataSource: NamedScheduleDataSource,
        settingsRepos: SettingsRepos,
        json: Json
    ): WidgetDataUpdaterImpl = WidgetDataUpdaterImpl(
        context = context,
        settingsRepos = settingsRepos,
        namedScheduleDataSource = namedScheduleDataSource,
        json = json
    )
}