package com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule

import androidx.annotation.Keep

@Keep
data class RecurrenceDto(
    val interval: Int,
    val currentNumber: Int,
    val firstWeekNumber: Int
)