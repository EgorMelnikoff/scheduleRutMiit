package com.egormelnikoff.schedulerutmiit.search.di

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.search.data.local.SearchQueryDao
import com.egormelnikoff.schedulerutmiit.search.data.local.SearchQueryDatabase
import com.egormelnikoff.schedulerutmiit.search.data.remote.SearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SearchModule {
    @Provides
    @Singleton
    fun provideSearchQueryDao(database: SearchQueryDatabase): SearchQueryDao = database.searchQueryDao()

    @Provides
    @Singleton
    fun provideSearchApi(retrofitBuilder: Retrofit.Builder): SearchApi =
        retrofitBuilder
            .baseUrl(Endpoints.BASE_RUT_MIIT_URL)
            .build()
            .create(SearchApi::class.java)

    @Provides
    @Singleton
    fun provideSearchQueryDatabase(
        @ApplicationContext context: Context
    ): SearchQueryDatabase =
        SearchQueryDatabase.getDatabase(context)

}