package com.egormelnikoff.schedulerutmiit.app.modules

import android.content.Context
import androidx.work.WorkManager
import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdaterImpl
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.datastore.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.shared_prefs.SharedPreferencesManager
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManagerImpl
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
    fun providePreferencesDataStore(@ApplicationContext context: Context): PreferencesDataStore {
        return PreferencesDataStore(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideResourcesManager(@ApplicationContext context: Context): ResourcesManager {
        return ResourcesManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideWidgetUpdater(
        @ApplicationContext context: Context,
        logger: Logger,
        scheduleRepos: ScheduleRepos,
        gson: Gson
    ): WidgetDataUpdaterImpl {
        return WidgetDataUpdaterImpl(
            context = context,
            logger = logger,
            scheduleRepos = scheduleRepos,
            gson = gson
        )
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
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
    fun provideLogger(@ApplicationContext context: Context, resourcesManager: ResourcesManager): Logger {
        return Logger(context, resourcesManager)
    }
}