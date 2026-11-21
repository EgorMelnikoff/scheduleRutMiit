package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model

import java.time.LocalDateTime

data class Event(
    val id: Long = 0,
    val scheduleId: Long = 0,
    val isHidden: Boolean,
    val isCustomEvent: Boolean,
    val startDatetime: LocalDateTime,
    val endDatetime: LocalDateTime,
    val interval: Int?,
    val periodNumber: Int?,
    val name: String,
    val typeName: String?,
    val timeSlotName: String?,
    val lecturers: List<Lecturer>?,
    val rooms: List<Room>?,
    val groups: List<Group>?
) {
    fun customHashCode(forceNonPeriodic: Boolean = false): Int {
        val hashString = when {
            forceNonPeriodic -> "$name$typeName${startDatetime.dayOfWeek}${startDatetime.toLocalTime()}$groups"
            (interval != null) -> "$name$typeName${startDatetime.dayOfWeek}${startDatetime.toLocalTime()}${interval}$periodNumber$groups"
            else -> "$name$typeName$startDatetime$groups"
        }
        return hashString.hashCode()
    }

    fun customEquals(other: Event): Boolean {
        return this.customHashCode() == other.customHashCode()
    }

}