package com.egormelnikoff.schedulerutmiit.tasks.di

import com.egormelnikoff.schedulerutmiit.tasks.data.repos.TaskReposImpl
import com.egormelnikoff.schedulerutmiit.tasks.domain.repos.TaskRepos
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TaskBindModule {
    @Binds
    @Singleton
    abstract fun bindTaskRepos(taskReposImpl: TaskReposImpl): TaskRepos
}