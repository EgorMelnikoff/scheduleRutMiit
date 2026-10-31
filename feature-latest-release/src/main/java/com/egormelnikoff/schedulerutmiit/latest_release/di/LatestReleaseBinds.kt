package com.egormelnikoff.schedulerutmiit.latest_release.di

import com.egormelnikoff.schedulerutmiit.latest_release.data.local.AppInfoProviderImpl
import com.egormelnikoff.schedulerutmiit.latest_release.data.repos.LatestReleaseReposImpl
import com.egormelnikoff.schedulerutmiit.latest_release.domain.repos.AppInfoProvider
import com.egormelnikoff.schedulerutmiit.latest_release.domain.repos.LatestReleaseRepos
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BindModule {
    @Binds
    @Singleton
    abstract fun bindAppInfoProvider(appInfoProviderImpl: AppInfoProviderImpl): AppInfoProvider


    @Binds
    @Singleton
    abstract fun bindLatestReleaseRepos(latestReleaseReposImpl: LatestReleaseReposImpl): LatestReleaseRepos
}
