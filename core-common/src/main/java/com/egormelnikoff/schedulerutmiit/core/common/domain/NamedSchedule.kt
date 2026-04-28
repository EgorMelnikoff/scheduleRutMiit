package com.egormelnikoff.schedulerutmiit.core.common.domain

import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import kotlinx.serialization.Serializable

@Serializable
data class NamedSchedule(
    val id: Long,
    val fullName: String,
    val shortName: String,
    val apiId: String?,
    val type: NamedScheduleType,
    val isDefault: Boolean,
    val lastTimeUpdate: Long
)