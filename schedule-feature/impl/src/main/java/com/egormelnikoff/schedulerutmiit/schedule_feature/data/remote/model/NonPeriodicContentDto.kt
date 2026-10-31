package com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NonPeriodicContentDto(
    @SerialName("events")
    val events: List<EventDto>?,
)