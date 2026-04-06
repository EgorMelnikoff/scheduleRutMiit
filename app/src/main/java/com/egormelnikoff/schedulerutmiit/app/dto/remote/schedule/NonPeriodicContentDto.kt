package com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.EventDto
import com.google.gson.annotations.SerializedName

@Keep
data class NonPeriodicContentDto(
    @SerializedName("events")
    val events: List<EventDto>?,
)