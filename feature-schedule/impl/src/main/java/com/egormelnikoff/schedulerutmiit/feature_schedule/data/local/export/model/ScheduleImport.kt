package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateSerializer
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.TimetableType
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ScheduleImport(
    val id: Long = 0,
    val namedScheduleId: Long,
    val timetableId: String,
    val timetableType: TimetableType,
    val downloadUrl: String?,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate,
    val recurrence: RecurrenceImport?,
    val isDefault: Boolean,
)