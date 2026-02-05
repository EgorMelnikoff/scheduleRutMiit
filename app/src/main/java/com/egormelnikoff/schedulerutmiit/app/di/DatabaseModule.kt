package com.egormelnikoff.schedulerutmiit.app.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.data.datasource.local.AppDatabase
import com.egormelnikoff.schedulerutmiit.data.datasource.local.Converters
import com.egormelnikoff.schedulerutmiit.data.datasource.local.Dao
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
    fun provideDao(database: AppDatabase): Dao = database.dao()


    @Provides
    @Singleton
    fun provideConverters(gson: Gson): Converters = Converters(gson)
}