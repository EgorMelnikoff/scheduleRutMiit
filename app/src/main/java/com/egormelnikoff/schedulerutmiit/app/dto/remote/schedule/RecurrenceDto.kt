package com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RecurrenceDto(
    @SerializedName("interval")
    val interval: Int,
    @SerializedName("currentNumber")
    val currentNumber: Int,
    val firstWeekNumber: Int
)