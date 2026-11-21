package com.egormelnikoff.schedulerutmiit.feature_curriculum.di

import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.repos.CurriculumReposImpl
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.repos.CurriculumRepos
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CurriculumBindModule {
    @Binds
    @Singleton
    abstract fun bindCurriculumRepos(curriculumReposImpl: CurriculumReposImpl): CurriculumRepos
}