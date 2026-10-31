package com.egormelnikoff.schedulerutmiit.schedule_feature.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.common.domain.repos.PreferencesRepos
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.NamedScheduleDataSource
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.ScheduleDatabase
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.converters.ScheduleConverters
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.ScheduleApi
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.utils.file.FileExporter
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.utils.file.FileImporter
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.utils.parser.ScheduleParser
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.widget.model.WidgetDataUpdaterImpl
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
    fun provideScheduleDatabase(
        @ApplicationContext context: Context,
        scheduleConverters: ScheduleConverters
    ): ScheduleDatabase =
        ScheduleDatabase.getDatabase(context, scheduleConverters)

    @Provides
    @Singleton
    fun provideNamedScheduleDao(database: ScheduleDatabase): NamedScheduleDao =
        database.namedScheduleDao()

    @Provides
    @Singleton
    fun provideScheduleDao(database: ScheduleDatabase): ScheduleDao = database.scheduleDao()

    @Provides
    @Singleton
    fun provideEventDao(database: ScheduleDatabase): EventDao = database.eventDao()

    @Provides
    @Singleton
    fun provideEventExtraDao(database: ScheduleDatabase): EventExtraDao = database.eventExtraDao()


    @Provides
    @Singleton
    fun provideConverters(json: Json): ScheduleConverters = ScheduleConverters(json)


    @Provides
    @Singleton
    fun provideFileExporter(@ApplicationContext context: Context) = FileExporter(context)

    @Provides
    @Singleton
    fun provideFileImporter(@ApplicationContext context: Context) = FileImporter(context)

    @Provides
    fun provideScheduleParser(): ScheduleParser = ScheduleParser

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
        preferencesRepos: PreferencesRepos,
        json: Json
    ): WidgetDataUpdaterImpl = WidgetDataUpdaterImpl(
        context = context,
        preferencesRepos = preferencesRepos,
        namedScheduleDataSource = namedScheduleDataSource,
        json = json
    )
}