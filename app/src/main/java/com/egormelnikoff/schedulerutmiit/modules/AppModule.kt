package com.egormelnikoff.schedulerutmiit.modules

import android.content.Context
import com.egormelnikoff.schedulerutmiit.data.datasource.local.prefs_datastore.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManagerImpl
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
    fun provideResourcesManager(@ApplicationContext context: Context): ResourcesManager {
        return ResourcesManagerImpl(context)
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
}