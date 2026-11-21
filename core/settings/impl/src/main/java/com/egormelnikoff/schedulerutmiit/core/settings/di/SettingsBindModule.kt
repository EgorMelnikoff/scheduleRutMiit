package com.egormelnikoff.schedulerutmiit.core.settings.di

import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.core.settings.data.repos.SettingsReposImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SettingsBindModule {
    @Binds
    @Singleton
    abstract fun bindSettingsRepos(settingsReposImpl: SettingsReposImpl): SettingsRepos
}