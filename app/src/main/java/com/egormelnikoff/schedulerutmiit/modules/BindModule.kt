package com.egormelnikoff.schedulerutmiit.modules

import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserImpl
import com.egormelnikoff.schedulerutmiit.data.repos.news.NewsRepos
import com.egormelnikoff.schedulerutmiit.data.repos.news.NewsReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchRepos
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.settings.SettingsRepos
import com.egormelnikoff.schedulerutmiit.data.repos.settings.SettingsReposImpl
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
    abstract fun bindSettingsRepos(settingsReposImpl: SettingsReposImpl): SettingsRepos

    @Binds
    @Singleton
    abstract fun bindNewsRepos(newsReposImpl: NewsReposImpl): NewsRepos

    @Binds
    @Singleton
    abstract fun bindParser(parserImpl: ParserImpl): Parser
}
