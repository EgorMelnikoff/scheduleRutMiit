package com.egormelnikoff.schedulerutmiit.schedule_feature.api.model

data class FetchScheduleEvent(
    val name: String,
    val apiId: Int,
    val type: NamedScheduleType
)