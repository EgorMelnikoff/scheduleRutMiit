package com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Keep
data class EventDto(
    @SerializedName("startDatetime")
    val startDatetime: LocalDateTime?,
    @SerializedName("endDatetime")
    val endDatetime: LocalDateTime?,
    @SerializedName("recurrenceRule")
    val recurrence: RecurrenceEventDto?,
    @SerializedName("periodNumber")
    val periodNumber: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("typeName")
    val typeName: String?,
    @SerializedName("timeSlotName")
    val timeSlotName: String?,
    @SerializedName("lecturers")
    val lecturers: List<LecturerDto>?,
    @SerializedName("rooms")
    val rooms: List<RoomDto>?,
    @SerializedName("groups")
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

    fun toEntity() = if (startDatetime != null && endDatetime != null && name != null)
        Event(
            startDatetime = startDatetime,
            endDatetime = endDatetime,
            recurrenceRule = recurrence,
            periodNumber = periodNumber,
            name = name,
            typeName = typeName,
            timeSlotName = timeSlotName,
            lecturers = lecturers,
            groups = groups,
            rooms = rooms
        )
    else throw NullPointerException()
}