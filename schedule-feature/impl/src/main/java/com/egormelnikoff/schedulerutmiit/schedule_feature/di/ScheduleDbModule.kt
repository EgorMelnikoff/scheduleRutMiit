package com.egormelnikoff.schedulerutmiit.schedule_feature.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.ScheduleDatabase
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.converters.ScheduleConverters
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ScheduleDbModule {
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
}