package com.egormelnikoff.schedulerutmiit.core.network.dto.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NonPeriodicContentDto(
    @SerialName("events")
    val events: List<EventDto>?,
)