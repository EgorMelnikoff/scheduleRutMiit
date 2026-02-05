package com.egormelnikoff.schedulerutmiit.app.di

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleTypeAdapter
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProviderEntryPoint {
    fun gson(): Gson {
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
                LocalDateTime::class.java,
                JsonDeserializer { json, _, _ ->
                    LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                }
            ).create()
    }

    fun widgetDataUpdater(): WidgetDataUpdater

    fun workScheduler(): WorkScheduler

    fun logger(): Logger
}