package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.v1.data

import com.egormelnikoff.schedulerutmiit.core.common.utils.serializers.LocalDateTimeSerializer
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.EventImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.GroupImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.LecturerImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.RoomImport
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
    val lecturers: List<LecturerImport>?,
    val rooms: List<RoomImport>?,
    val groups: List<GroupImport>?
) {
    fun toEvent() = EventImport(
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