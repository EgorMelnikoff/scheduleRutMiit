package com.egormelnikoff.schedulerutmiit.core.common.domain

import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val version: Int,
    val namedSchedules: List<NamedSchedule>,
    val schedules: List<Schedule>,
    val events: List<Event>,
    val eventsExtraData: List<EventExtraData>
)