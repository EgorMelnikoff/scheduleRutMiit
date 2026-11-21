package com.egormelnikoff.schedulerutmiit.feature_schedule.api.model

data class FetchScheduleEvent(
    val name: String,
    val apiId: Int,
    val type: NamedScheduleType
)