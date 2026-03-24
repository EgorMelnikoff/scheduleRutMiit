package com.egormelnikoff.schedulerutmiit.app.di

import com.egormelnikoff.schedulerutmiit.data.repos.news.NewsRepos
import com.egormelnikoff.schedulerutmiit.data.repos.news.NewsReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.local.ScheduleLocalRepos
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.local.ScheduleLocalReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.remote.ScheduleRemoteRepos
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.remote.ScheduleRemoteReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.search.local.SearchLocalRepos
import com.egormelnikoff.schedulerutmiit.data.repos.search.local.SearchLocalReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.search.remote.SearchRemoteRepos
import com.egormelnikoff.schedulerutmiit.data.repos.search.remote.SearchRemoteReposImpl
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
    abstract fun bindScheduleLocal(scheduleLocalDataSourceImpl: ScheduleLocalReposImpl): ScheduleLocalRepos

    @Binds
    @Singleton
    abstract fun bindScheduleRemote(scheduleRemoteDataSourceImpl: ScheduleRemoteReposImpl): ScheduleRemoteRepos

    @Binds
    @Singleton
    abstract fun bindSearchLocal(searchLocalReposImpl: SearchLocalReposImpl): SearchLocalRepos

    @Binds
    @Singleton
    abstract fun bindSearchRemote(searchRemoteReposImpl: SearchRemoteReposImpl): SearchRemoteRepos


    @Binds
    @Singleton
    abstract fun bindNewsRepos(newsReposImpl: NewsReposImpl): NewsRepos
}
