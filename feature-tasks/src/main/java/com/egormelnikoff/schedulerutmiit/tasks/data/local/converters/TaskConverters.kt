package com.egormelnikoff.schedulerutmiit.tasks.data.local.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@ProvidedTypeConverter
internal class TaskConverters @Inject constructor() {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()


    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let(LocalTime::parse)
}