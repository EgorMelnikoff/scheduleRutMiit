package com.egormelnikoff.schedulerutmiit.core.network.dto.timetable

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TimetablesDto(
    @SerialName("timetables")
    val timetables: List<TimetableDto>
)