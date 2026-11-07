package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PeriodicContentDto(
    @SerialName("events")
    val events: List<EventDto>?,
    @SerialName("recurrence")
    val recurrence: RecurrenceDto
)