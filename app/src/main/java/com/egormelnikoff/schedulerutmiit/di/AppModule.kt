package com.egormelnikoff.schedulerutmiit.di

import android.content.Context
import androidx.work.WorkManager
import com.egormelnikoff.schedulerutmiit.app.preferences.PreferencesDataSourceImpl
import com.egormelnikoff.schedulerutmiit.app.widget.data.WidgetDataUpdaterImpl
import com.egormelnikoff.schedulerutmiit.core.common.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateSerializer
import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeSerializer
import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.parser.SubjectsListParser
import com.egormelnikoff.schedulerutmiit.latest_release.data.repos.AppInfoProviderImpl
import com.egormelnikoff.schedulerutmiit.news.data.parser.NewsParser
import com.egormelnikoff.schedulerutmiit.schedule.data.parser.ScheduleParser
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.search.data.parser.SearchParser
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
    fun providePreferencesRepos(@ApplicationContext context: Context, json: Json): PreferencesDataSourceImpl =
        PreferencesDataSourceImpl(context, json)

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
        preferencesRepos: PreferencesDataSourceImpl,
        json: Json
    ): WidgetDataUpdaterImpl = WidgetDataUpdaterImpl(
        context = context,
        preferencesDataSource = preferencesRepos,
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
    fun provideNewsParser(): NewsParser = NewsParser

    @Provides
    fun provideScheduleParser(): ScheduleParser = ScheduleParser

    @Provides
    fun provideSearchParser(): SearchParser = SearchParser

    @Provides
    fun provideSubjectsListParser(): SubjectsListParser = SubjectsListParser
}