package com.egormelnikoff.schedulerutmiit.core.common.dto

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RecurrenceDto(
    @SerialName("interval")
    val interval: Int,
    @SerialName("currentNumber")
    val currentNumber: Int,
    val firstWeekNumber: Int
)