package com.egormelnikoff.schedulerutmiit.core.common.dto.schedule

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.core.common.dto.schedule.RecurrenceDto
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