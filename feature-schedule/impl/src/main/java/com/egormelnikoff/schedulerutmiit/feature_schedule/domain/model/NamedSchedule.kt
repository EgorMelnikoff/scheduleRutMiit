package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model

import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.ScheduleType
import java.time.LocalDateTime

internal data class NamedSchedule(
    val id: Long,
    val fullName: String,
    val shortName: String,
    val apiId: String?,
    val type: ScheduleType,
    val isDefault: Boolean,
    val lastTimeUpdate: LocalDateTime
)