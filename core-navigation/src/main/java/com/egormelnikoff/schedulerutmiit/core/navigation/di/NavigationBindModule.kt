package com.egormelnikoff.schedulerutmiit.core.navigation.di

import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.impl.AppRouterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationBindModule {

    @Binds
    @Singleton
    abstract fun bindAppRouter(appRouterImpl: AppRouterImpl): AppRouter
}
