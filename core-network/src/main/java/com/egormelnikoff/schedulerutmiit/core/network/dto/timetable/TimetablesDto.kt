package com.egormelnikoff.schedulerutmiit.core.network.dto.timetable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimetablesDto(
    @SerialName("timetables")
    val timetables: List<TimetableDto>
)