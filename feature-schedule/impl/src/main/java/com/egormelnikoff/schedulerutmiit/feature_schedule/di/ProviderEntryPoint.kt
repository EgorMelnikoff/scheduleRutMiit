package com.egormelnikoff.schedulerutmiit.feature_schedule.di

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateSerializer
import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeSerializer
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.work.ScheduleWorkScheduler
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.WidgetDataUpdater
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

    fun workScheduler(): ScheduleWorkScheduler
}