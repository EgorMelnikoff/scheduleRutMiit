package com.egormelnikoff.schedulerutmiit.app.di

import android.content.Context
import androidx.work.WorkManager
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleTypeAdapter
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.app.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): PreferencesDataStore =
        PreferencesDataStore(context)

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
        scheduleRepos: ScheduleRepos,
        gson: Gson
    ): WidgetDataUpdater = WidgetDataUpdater(
        context = context,
        scheduleRepos = scheduleRepos,
        gson = gson
    )

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                NamedScheduleType::class.java,
                NamedScheduleTypeAdapter()
            )
            .registerTypeAdapter(
                LocalDate::class.java,
                JsonDeserializer { json, _, _ ->
                    LocalDate.parse(json.asString, DateTimeFormatter.ISO_DATE)
                }
            )
            .registerTypeAdapter(
                LocalDate::class.java,
                JsonSerializer<LocalDate> { src, _, _ ->
                    JsonPrimitive(DateTimeFormatter.ISO_DATE.format(src))
                }
            )
            .registerTypeAdapter(
                LocalDateTime::class.java,
                JsonDeserializer { json, _, _ ->
                    LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                }
            ).registerTypeAdapter(
                LocalDateTime::class.java,
                JsonSerializer<LocalDateTime> { src, _, _ ->
                    JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(src))
                }
            ).create()
    }

    @Provides
    @Singleton
    fun provideLogger(
        @ApplicationContext context: Context,
        resourcesManager: ResourcesManager
    ): Logger = Logger(context, resourcesManager)
}