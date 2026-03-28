package com.egormelnikoff.schedulerutmiit.app.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.datasource.local.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.datasource.local.db.Converters
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.SearchQueryDao
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context, converters: Converters): AppDatabase =
        AppDatabase.getDatabase(context, converters)

    @Provides
    @Singleton
    fun provideNamedScheduleDao(database: AppDatabase): SearchQueryDao = database.searchQueryDao()

    @Provides
    @Singleton
    fun provideScheduleDao(database: AppDatabase): NamedScheduleDao = database.namedScheduleDao()

    @Provides
    @Singleton
    fun provideEventDao(database: AppDatabase): ScheduleDao = database.scheduleDao()

    @Provides
    @Singleton
    fun provideEventExtraDao(database: AppDatabase): EventDao = database.eventDao()

    @Provides
    @Singleton
    fun provideSearchQueryDao(database: AppDatabase): EventExtraDao = database.eventExtraDao()

    @Provides
    @Singleton
    fun provideConverters(gson: Gson): Converters = Converters(gson)
}