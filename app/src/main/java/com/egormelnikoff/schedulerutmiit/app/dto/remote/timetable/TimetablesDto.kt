package com.egormelnikoff.schedulerutmiit.app.dto.remote.timetable

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TimetablesDto(
    @SerialName("timetables")
    val timetables: List<TimetableDto>
)