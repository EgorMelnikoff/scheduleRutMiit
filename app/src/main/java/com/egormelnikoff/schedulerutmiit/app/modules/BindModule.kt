package com.egormelnikoff.schedulerutmiit.app.modules

import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdaterImpl
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.app.work.WorkSchedulerImpl
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManagerImpl
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserImpl
import com.egormelnikoff.schedulerutmiit.data.repos.news.NewsRepos
import com.egormelnikoff.schedulerutmiit.data.repos.news.NewsReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchRepos
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchReposImpl
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
    abstract fun bindScheduleRepos(scheduleReposImpl: ScheduleReposImpl): ScheduleRepos

    @Binds
    @Singleton
    abstract fun bindSearchRepos(searchReposImpl: SearchReposImpl): SearchRepos


    @Binds
    @Singleton
    abstract fun bindNewsRepos(newsReposImpl: NewsReposImpl): NewsRepos

    @Binds
    @Singleton
    abstract fun bindWidgetUpdater(widgetDataUpdaterImpl: WidgetDataUpdaterImpl): WidgetDataUpdater

    @Binds
    @Singleton
    abstract fun bindWorkScheduler(workSchedulerImpl: WorkSchedulerImpl): WorkScheduler

    @Binds
    @Singleton
    abstract fun bindParser(parserImpl: ParserImpl): Parser

    @Binds
    @Singleton
    abstract fun bindResourcesManager(resourcesManagerImpl: ResourcesManagerImpl): ResourcesManager
}
