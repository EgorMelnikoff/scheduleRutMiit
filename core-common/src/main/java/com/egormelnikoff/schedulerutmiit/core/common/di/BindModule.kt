package com.egormelnikoff.schedulerutmiit.core.common.di

import com.egormelnikoff.schedulerutmiit.core.common.data.repos.PreferencesReposImpl
import com.egormelnikoff.schedulerutmiit.core.common.domain.repos.PreferencesRepos
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {

    @Binds
    @Singleton
    abstract fun bindPreferences(preferencesRepos: PreferencesReposImpl): PreferencesRepos
}