package com.egormelnikoff.schedulerutmiit.news.di

import com.egormelnikoff.schedulerutmiit.news.data.repos.NewsReposImpl
import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRepos
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
    abstract fun bindNewsRepos(newsReposImpl: NewsReposImpl): NewsRepos
}