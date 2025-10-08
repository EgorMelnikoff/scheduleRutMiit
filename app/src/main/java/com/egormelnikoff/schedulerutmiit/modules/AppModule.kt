package com.egormelnikoff.schedulerutmiit.modules

import android.content.Context
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.datasource.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.datasource.resources.ResourcesManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): PreferencesDataStore {
        return PreferencesDataStore(context)
    }

    @Provides
    @Singleton
    fun provideResourcesManager(@ApplicationContext context: Context): ResourcesManager {
        return ResourcesManagerImpl(context)
    }
}