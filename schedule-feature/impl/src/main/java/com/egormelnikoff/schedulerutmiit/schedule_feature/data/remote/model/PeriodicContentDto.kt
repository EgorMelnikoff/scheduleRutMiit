package com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PeriodicContentDto(
    @SerialName("events")
    val events: List<EventDto>?,
    @SerialName("recurrence")
    val recurrence: RecurrenceDto
)