package com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.EventDto
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