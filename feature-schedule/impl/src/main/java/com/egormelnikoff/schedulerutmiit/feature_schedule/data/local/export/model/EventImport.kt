package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class EventImport(
    val id: Long = 0,
    val scheduleId: Long = 0,
    val isHidden: Boolean,
    val isCustomEvent: Boolean,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startDatetime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDatetime: LocalDateTime,
    val interval: Int?,
    val periodNumber: Int?,
    val name: String,
    val typeName: String?,
    val timeSlotName: String?,
    val lecturers: List<LecturerImport>?,
    val rooms: List<RoomImport>?,
    val groups: List<GroupImport>?
) {
}