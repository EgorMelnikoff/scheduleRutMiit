package com.egormelnikoff.schedulerutmiit.tasks.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.tasks.data.local.TaskDatabase
import com.egormelnikoff.schedulerutmiit.tasks.data.local.converters.TaskConverters
import com.egormelnikoff.schedulerutmiit.tasks.data.local.dao.TaskCompletionDao
import com.egormelnikoff.schedulerutmiit.tasks.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object TaskModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        converters: TaskConverters
    ): TaskDatabase =
        TaskDatabase.getDatabase(context, converters)

    @Provides
    @Singleton
    fun provideTaskDao(database: TaskDatabase): TaskDao = database.taskDao()

    @Provides
    @Singleton
    fun provideTaskCompletionDao(database: TaskDatabase): TaskCompletionDao =
        database.taskCompletionDao()

}