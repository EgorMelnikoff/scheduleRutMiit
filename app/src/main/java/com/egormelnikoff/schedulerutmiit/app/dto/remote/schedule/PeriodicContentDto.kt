package com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.EventDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class PeriodicContentDto(
    @SerialName("events")
    val events: List<EventDto>?,
    @SerialName("recurrence")
    val recurrence: RecurrenceDto
)