package com.egormelnikoff.schedulerutmiit.core.navigation.di

import com.egormelnikoff.schedulerutmiit.core.navigation.impl.AppRouterImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideAppRouter() = AppRouterImpl()
}