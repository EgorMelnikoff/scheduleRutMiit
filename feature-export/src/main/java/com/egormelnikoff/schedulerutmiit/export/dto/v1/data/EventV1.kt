package com.egormelnikoff.schedulerutmiit.export.dto.v1.data

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class EventV1(
    val id: Long,
    val scheduleId: Long,
    val isHidden: Boolean = false,
    val isCustomEvent: Boolean = false,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startDatetime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDatetime: LocalDateTime,
    val recurrenceRule: RecurrenceEventExport?,
    val periodNumber: Int?,
    val name: String,
    val typeName: String?,
    val timeSlotName: String?,
    val lecturers: List<Lecturer>?,
    val rooms: List<Room>?,
    val groups: List<Group>?
) {
    fun toEvent() = Event(
        id,
        scheduleId,
        isHidden,
        isCustomEvent,
        startDatetime,
        endDatetime,
        recurrenceRule?.interval,
        periodNumber,
        name,
        typeName,
        timeSlotName,
        lecturers,
        rooms,
        groups
    )

}