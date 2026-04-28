package com.egormelnikoff.schedulerutmiit.core.network.dto.schedule

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.core.common.domain.Recurrence
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

fun RecurrenceDto.toDomain() = Recurrence(
    interval, currentNumber, firstWeekNumber
)