package com.egormelnikoff.schedulerutmiit.schedule_feature.api.model

data class NamedSchedule(
    val id: Long,
    val fullName: String,
    val shortName: String,
    val apiId: String?,
    val type: NamedScheduleType,
    val isDefault: Boolean,
    val lastTimeUpdate: Long
)