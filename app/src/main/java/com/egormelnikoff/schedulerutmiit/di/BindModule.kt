package com.egormelnikoff.schedulerutmiit.di

import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.repos.CurriculumRemoteDataSourceImpl
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.repos.CurriculumRemoteDataSource
import com.egormelnikoff.schedulerutmiit.latest_release.data.repos.AppInfoProviderImpl
import com.egormelnikoff.schedulerutmiit.latest_release.data.repos.LatestReleaseDataSourceImpl
import com.egormelnikoff.schedulerutmiit.latest_release.domain.repos.AppInfoProvider
import com.egormelnikoff.schedulerutmiit.latest_release.domain.repos.LatestReleaseDataSource
import com.egormelnikoff.schedulerutmiit.news.data.repos.NewsRemoteDataSourceImpl
import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRemoteDataSource
import com.egormelnikoff.schedulerutmiit.schedule.data.repos.EventExtraReposImpl
import com.egormelnikoff.schedulerutmiit.schedule.data.repos.EventReposImpl
import com.egormelnikoff.schedulerutmiit.schedule.data.repos.NamedScheduleReposImpl
import com.egormelnikoff.schedulerutmiit.schedule.data.repos.ScheduleRemoteDataSourceImpl
import com.egormelnikoff.schedulerutmiit.schedule.data.repos.ScheduleReposImpl
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventExtraRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRemoteDataSource
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.search.data.repos.SearchQueryReposImpl
import com.egormelnikoff.schedulerutmiit.search.data.repos.SearchRemoteDataSourceImpl
import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchQueryRepos
import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchRemoteDataSource
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
    abstract fun bindScheduleRemoteDataSource(scheduleRemoteDataSourceImpl: ScheduleRemoteDataSourceImpl): ScheduleRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindNewsRemoteDataSource(newsRemoteDataSourceImpl: NewsRemoteDataSourceImpl): NewsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindSearchRemoteDataSource(searchRemoteDataSourceImpl: SearchRemoteDataSourceImpl): SearchRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCurriculumRemoteDataSource(curriculumRemoteDataSourceImpl: CurriculumRemoteDataSourceImpl): CurriculumRemoteDataSource


    @Binds
    @Singleton
    abstract fun bindLatestReleaseDataSource(latestReleaseDataSourceImpl: LatestReleaseDataSourceImpl): LatestReleaseDataSource

    @Binds
    @Singleton
    abstract fun bindNamedScheduleRepos(namedScheduleReposImpl: NamedScheduleReposImpl): NamedScheduleRepos

    @Binds
    @Singleton
    abstract fun bindScheduleRepos(scheduleReposImpl: ScheduleReposImpl): ScheduleRepos

    @Binds
    @Singleton
    abstract fun bindEventRepos(eventReposImpl: EventReposImpl): EventRepos

    @Binds
    @Singleton
    abstract fun bindEventExtraRepos(eventExtraReposImpl: EventExtraReposImpl): EventExtraRepos


    @Binds
    @Singleton
    abstract fun bindSearchQueryRepos(searchQueryReposImpl: SearchQueryReposImpl): SearchQueryRepos



    @Binds
    @Singleton
    abstract fun bindAppInfoProvider(appInfoProviderImpl: AppInfoProviderImpl): AppInfoProvider
}
