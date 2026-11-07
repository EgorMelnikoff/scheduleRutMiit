package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model

import java.time.LocalDate

data class Schedule(
    val id: Long = 0,
    val namedScheduleId: Long,
    val timetableId: String,
    val timetableType: TimetableType,
    val downloadUrl: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val recurrence: Recurrence?,
    val isDefault: Boolean,
) {
    fun getKey(): Int = "$startDate${timetableType.name}".hashCode()

}