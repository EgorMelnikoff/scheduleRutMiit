package com.egormelnikoff.schedulerutmiit.core.common.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.common.data.repos.PreferencesReposImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {
    @Provides
    @Singleton
    fun providePreferencesRepos(
        @ApplicationContext context: Context
    ): PreferencesReposImpl =
        PreferencesReposImpl(context)
}