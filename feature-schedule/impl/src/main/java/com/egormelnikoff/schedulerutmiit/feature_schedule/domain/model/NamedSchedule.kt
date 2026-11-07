package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model

import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType

data class NamedSchedule(
    val id: Long,
    val fullName: String,
    val shortName: String,
    val apiId: String?,
    val type: NamedScheduleType,
    val isDefault: Boolean,
    val lastTimeUpdate: Long
)