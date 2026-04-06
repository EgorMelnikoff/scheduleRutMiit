package com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RecurrenceEventDto(
    @SerializedName("frequency")
    val frequency: String,
    @SerializedName("interval")
    val interval: Int
)