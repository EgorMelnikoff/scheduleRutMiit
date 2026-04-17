package com.egormelnikoff.schedulerutmiit.di

import android.content.Context
import androidx.work.WorkManager
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.local.parser.NewsParser
import com.egormelnikoff.schedulerutmiit.data.local.parser.ScheduleParser
import com.egormelnikoff.schedulerutmiit.data.local.parser.SearchParser
import com.egormelnikoff.schedulerutmiit.data.local.parser.SubjectsListParser
import com.egormelnikoff.schedulerutmiit.data.local.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.local.serializers.LocalDateSerializer
import com.egormelnikoff.schedulerutmiit.data.local.serializers.LocalDateTimeSerializer
import com.egormelnikoff.schedulerutmiit.data.remote.network.logger.Logger
import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.use_case.updates.AppInfoProviderImpl
import com.egormelnikoff.schedulerutmiit.ui.widget.WidgetDataUpdater
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppProvider(@ApplicationContext context: Context): AppInfoProviderImpl =
        AppInfoProviderImpl(context)

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context, json: Json): PreferencesDataStore =
        PreferencesDataStore(context, json)

    @Provides
    @Singleton
    fun provideResourcesManager(@ApplicationContext context: Context): ResourcesManager =
        ResourcesManager(context)


    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideWidgetUpdater(
        @ApplicationContext context: Context,
        namedScheduleRepos: NamedScheduleRepos,
        json: Json
    ): WidgetDataUpdater = WidgetDataUpdater(
        context = context,
        namedScheduleRepos = namedScheduleRepos,
        json = json
    )

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            serializersModule = SerializersModule {
                contextual(LocalDate::class, LocalDateSerializer)
                contextual(LocalDateTime::class, LocalDateTimeSerializer)
            }
        }
    }

    @Provides
    @Singleton
    fun provideLogger(
        @ApplicationContext context: Context,
        resourcesManager: ResourcesManager
    ): Logger = Logger(context, resourcesManager)

    @Provides
    fun provideNewsParser(): NewsParser = NewsParser

    @Provides
    fun provideScheduleParser(): ScheduleParser = ScheduleParser

    @Provides
    fun provideSearchParser(): SearchParser = SearchParser

    @Provides
    fun provideSubjectsListParser(): SubjectsListParser = SubjectsListParser
}