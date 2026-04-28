package com.egormelnikoff.schedulerutmiit.core.common.domain

import androidx.annotation.Keep

@Keep
data class ScheduleWithEvents(
    val schedule: Schedule,
    val events: List<Event>,
    val eventsExtraData: List<EventExtraData> = listOf()
)