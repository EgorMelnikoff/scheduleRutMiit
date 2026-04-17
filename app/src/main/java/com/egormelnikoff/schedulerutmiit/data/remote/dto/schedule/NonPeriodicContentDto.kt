package com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NonPeriodicContentDto(
    @SerialName("events")
    val events: List<com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.EventDto>?,
)