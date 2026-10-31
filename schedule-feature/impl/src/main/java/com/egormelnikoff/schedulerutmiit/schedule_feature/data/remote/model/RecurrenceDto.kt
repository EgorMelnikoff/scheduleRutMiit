package com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecurrenceDto(
    @SerialName("interval")
    val interval: Int,
    @SerialName("currentNumber")
    val currentNumber: Int
)

