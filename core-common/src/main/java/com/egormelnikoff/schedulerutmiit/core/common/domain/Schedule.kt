package com.egormelnikoff.schedulerutmiit.core.common.domain

import androidx.room.Embedded
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Schedule(
    val id: Long = 0,
    val namedScheduleId: Long,
    val timetableId: String,
    val timetableType: TimetableType,
    val downloadUrl: String?,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate,
    @Embedded
    val recurrence: Recurrence?,
    val isDefault: Boolean = false,
) {
    fun getKey(): Int = "$startDate${timetableType.name}".hashCode()

}