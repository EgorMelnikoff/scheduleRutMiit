package com.egormelnikoff.schedulerutmiit.modules

import android.content.Context
import com.egormelnikoff.schedulerutmiit.data.datasource.local.database.AppDatabase
import com.egormelnikoff.schedulerutmiit.data.datasource.local.database.Converters
import com.egormelnikoff.schedulerutmiit.data.datasource.local.database.NamedScheduleDao
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
    fun provideAppDatabase(@ApplicationContext context: Context, converters: Converters): AppDatabase {
        return AppDatabase.getDatabase(context, converters)
    }

    @Provides
    @Singleton
    fun provideNamedScheduleDao(database: AppDatabase): NamedScheduleDao {
        return database.namedScheduleDao()
    }

    @Provides
    @Singleton
    fun provideConverters(gson: Gson): Converters {
        return Converters(gson)
    }
}