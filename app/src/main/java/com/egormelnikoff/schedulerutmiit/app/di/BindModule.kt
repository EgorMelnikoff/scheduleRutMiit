package com.egormelnikoff.schedulerutmiit.app.di

import com.egormelnikoff.schedulerutmiit.datasource.remote.news.NewsRemoteDataSource
import com.egormelnikoff.schedulerutmiit.datasource.remote.news.NewsRemoteDataSourceImpl
import com.egormelnikoff.schedulerutmiit.datasource.remote.schedule.ScheduleRemoteDataSource
import com.egormelnikoff.schedulerutmiit.datasource.remote.schedule.ScheduleRemoteDataSourceImpl
import com.egormelnikoff.schedulerutmiit.datasource.remote.search.SearchRemoteDataSource
import com.egormelnikoff.schedulerutmiit.datasource.remote.search.SearchRemoteDataSourceImpl
import com.egormelnikoff.schedulerutmiit.repos.event.EventRepos
import com.egormelnikoff.schedulerutmiit.repos.event.EventReposImpl
import com.egormelnikoff.schedulerutmiit.repos.event_extra.EventExtraRepos
import com.egormelnikoff.schedulerutmiit.repos.event_extra.EventExtraReposImpl
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleReposImpl
import com.egormelnikoff.schedulerutmiit.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.repos.schedule.ScheduleReposImpl
import com.egormelnikoff.schedulerutmiit.repos.search_query.SearchQueryRepos
import com.egormelnikoff.schedulerutmiit.repos.search_query.SearchQueryReposImpl
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
}
