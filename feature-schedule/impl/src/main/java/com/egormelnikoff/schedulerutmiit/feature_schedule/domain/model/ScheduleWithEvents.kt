package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model

internal data class ScheduleWithEvents(
    val schedule: Schedule,
    val events: List<Event>,
    val eventsExtraData: List<EventExtraData> = listOf()
)