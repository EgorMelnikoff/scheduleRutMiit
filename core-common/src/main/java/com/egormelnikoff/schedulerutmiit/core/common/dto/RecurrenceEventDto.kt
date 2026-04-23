package com.egormelnikoff.schedulerutmiit.core.common.dto

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RecurrenceEventDto(
    @SerialName("frequency")
    val frequency: String,
    @SerialName("interval")
    val interval: Int
)