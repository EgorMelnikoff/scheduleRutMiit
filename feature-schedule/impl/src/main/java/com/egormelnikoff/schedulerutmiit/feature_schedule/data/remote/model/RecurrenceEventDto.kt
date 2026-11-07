package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecurrenceEventDto(
    @SerialName("frequency")
    val frequency: String,
    @SerialName("interval")
    val interval: Int
)