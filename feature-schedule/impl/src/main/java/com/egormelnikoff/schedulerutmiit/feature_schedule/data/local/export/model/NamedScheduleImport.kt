package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model

import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.ScheduleType
import kotlinx.serialization.Serializable

@Serializable
internal data class NamedScheduleImport(
    val id: Long,
    val fullName: String,
    val shortName: String,
    val apiId: String?,
    val type: ScheduleType,
    val isDefault: Boolean,
    val lastTimeUpdate: Long
)