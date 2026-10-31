package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model

import com.egormelnikoff.schedulerutmiit.core.common.utils.serializers.LocalDateSerializer
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.TimetableType
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