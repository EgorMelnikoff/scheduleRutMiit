package com.egormelnikoff.schedulerutmiit.core.common.domain

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Event(
    val id: Long = 0,
    val scheduleId: Long = -1,
    val isHidden: Boolean = false,
    val isCustomEvent: Boolean = false,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startDatetime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDatetime: LocalDateTime,
    val recurrenceRule: RecurrenceEvent?,
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
            (recurrenceRule != null) -> "$name$typeName${startDatetime.dayOfWeek}${startDatetime.toLocalTime()}${recurrenceRule.interval}$periodNumber$groups"
            else -> "$name$typeName$startDatetime$groups"
        }
        return hashString.hashCode()
    }

    fun customEquals(other: Event): Boolean {
        return this.customHashCode() == other.customHashCode()
    }

}