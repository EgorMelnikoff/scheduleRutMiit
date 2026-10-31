package com.egormelnikoff.schedulerutmiit.schedule_feature.api.model

data class ScheduleWithEvents(
    val schedule: Schedule,
    val events: List<Event>,
    val eventsExtraData: List<EventExtraData> = listOf()
)