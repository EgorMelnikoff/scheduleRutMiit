package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model

import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleType
import kotlinx.serialization.Serializable

@Serializable
data class NamedScheduleImport(
    val id: Long,
    val fullName: String,
    val shortName: String,
    val apiId: String?,
    val type: NamedScheduleType,
    val isDefault: Boolean,
    val lastTimeUpdate: Long
)