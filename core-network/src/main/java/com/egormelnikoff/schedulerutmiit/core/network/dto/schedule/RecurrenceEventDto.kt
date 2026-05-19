package com.egormelnikoff.schedulerutmiit.core.network.dto.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecurrenceEventDto(
    @SerialName("frequency")
    val frequency: String,
    @SerialName("interval")
    val interval: Int
)