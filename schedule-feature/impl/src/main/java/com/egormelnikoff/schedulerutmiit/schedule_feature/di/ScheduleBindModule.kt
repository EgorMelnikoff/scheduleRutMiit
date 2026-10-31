package com.egormelnikoff.schedulerutmiit.schedule_feature.di

import com.egormelnikoff.schedulerutmiit.schedule_feature.api.cache.FetchScheduleCache
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.repos.ExportReposImpl
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.cache.FetchScheduleCacheImpl
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.repos.NamedScheduleReposImpl
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos.ExportRepos
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.schedule_feature.glance.model.WidgetDataUpdaterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindScheduleModule {
    @Binds
    @Singleton
    abstract fun bindDataRepos(dataReposImpl: ExportReposImpl): ExportRepos

    @Binds
    @Singleton
    abstract fun bindWidgetDataUpdater(widgetDataUpdaterImpl: WidgetDataUpdaterImpl): WidgetDataUpdater

    @Binds
    @Singleton
    abstract fun bindNamedScheduleRepos(namedScheduleReposImpl: NamedScheduleReposImpl): NamedScheduleRepos

    @Binds
    @Singleton
    abstract fun bindFetchScheduleTrigger(fetchScheduleTriggerImpl: FetchScheduleCacheImpl): FetchScheduleCache

}