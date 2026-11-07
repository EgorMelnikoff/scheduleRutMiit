package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecurrenceDto(
    @SerialName("interval")
    val interval: Int,
    @SerialName("currentNumber")
    val currentNumber: Int
)

