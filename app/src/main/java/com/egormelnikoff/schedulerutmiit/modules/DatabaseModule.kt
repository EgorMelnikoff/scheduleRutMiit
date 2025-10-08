package com.egormelnikoff.schedulerutmiit.modules

import android.content.Context
import com.egormelnikoff.schedulerutmiit.data.datasource.local.AppDatabase
import com.egormelnikoff.schedulerutmiit.data.datasource.local.NamedScheduleDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideNamedScheduleDao(database: AppDatabase): NamedScheduleDao {
        return database.namedScheduleDao()
    }
}