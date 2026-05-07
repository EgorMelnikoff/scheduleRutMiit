package com.egormelnikoff.schedulerutmiit.core.common.domain

data class ExportData(
    val version: Int,
    val namedSchedules: List<NamedSchedule>,
    val schedules: List<Schedule>,
    val events: List<Event>,
    val eventsExtraData: List<EventExtraData>
)