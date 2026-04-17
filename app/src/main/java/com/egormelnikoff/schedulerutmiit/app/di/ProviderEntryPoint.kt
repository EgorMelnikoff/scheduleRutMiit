package com.egormelnikoff.schedulerutmiit.app.di

import com.egormelnikoff.schedulerutmiit.app.network.logger.Logger
import com.egormelnikoff.schedulerutmiit.app.serializers.LocalDateSerializer
import com.egormelnikoff.schedulerutmiit.app.serializers.LocalDateTimeSerializer
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.LocalDate
import java.time.LocalDateTime

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProviderEntryPoint {
    fun json(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            serializersModule = SerializersModule {
                contextual(LocalDate::class, LocalDateSerializer)
                contextual(LocalDateTime::class, LocalDateTimeSerializer)
            }
        }
    }

    fun widgetDataUpdater(): WidgetDataUpdater

    fun workScheduler(): WorkScheduler

    fun logger(): Logger
}