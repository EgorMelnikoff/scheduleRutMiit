package com.egormelnikoff.schedulerutmiit.core.network.dto.schedule

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeOffsetSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class EventDto(
    @Serializable(with = LocalDateTimeOffsetSerializer::class)
    @SerialName("startDatetime")
    val startDatetime: LocalDateTime?,
    @Serializable(with = LocalDateTimeOffsetSerializer::class)
    @SerialName("endDatetime")
    val endDatetime: LocalDateTime?,
    @SerialName("recurrenceRule")
    val recurrence: RecurrenceEventDto?,
    @SerialName("periodNumber")
    val periodNumber: Int?,
    @SerialName("name")
    val name: String?,
    @SerialName("typeName")
    val typeName: String?,
    @SerialName("timeSlotName")
    val timeSlotName: String?,
    @SerialName("lecturers")
    val lecturers: List<LecturerDto>?,
    @SerialName("rooms")
    val rooms: List<RoomDto>?,
    @SerialName("groups")
    val groups: List<GroupDto>?
) {
    fun customHashCode(forceNonPeriodic: Boolean = false): Int {
        val hashString = when {
            forceNonPeriodic -> "$name$typeName${startDatetime?.dayOfWeek}${startDatetime?.toLocalTime()}$groups"
            (recurrence != null) -> "$name$typeName${startDatetime?.dayOfWeek}${startDatetime?.toLocalTime()}${recurrence.interval}$periodNumber$groups"
            else -> "$name$typeName$startDatetime$groups"
        }
        return hashString.hashCode()
    }
}

