package com.egormelnikoff.schedulerutmiit.search.di

import com.egormelnikoff.schedulerutmiit.search.data.repos.SearchReposImpl
import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchRepos
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BindSearchModule {
    @Binds
    @Singleton
    abstract fun bindSearchRepos(searchRepos: SearchReposImpl): SearchRepos
}