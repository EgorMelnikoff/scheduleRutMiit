package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Deprecated("Новая ручка")
@Serializable
data class TimetablesDto(
    @SerialName("timetables")
    val timetables: List<TimetableDto>
)