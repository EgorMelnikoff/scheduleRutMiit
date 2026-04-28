package com.egormelnikoff.schedulerutmiit.core.common.domain

data class ScheduleWithEvents(
    val schedule: Schedule,
    val events: List<Event>,
    val eventsExtraData: List<EventExtraData> = listOf()
)