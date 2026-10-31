package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model

data class ScheduleWithEvents(
    val schedule: Schedule,
    val events: List<Event>,
    val eventsExtraData: List<EventExtraData> = listOf()
)