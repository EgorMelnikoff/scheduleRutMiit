package com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDto(
    @SerialName("timetable")
    val timetable: TimetableDto,
    @SerialName("periodicContent")
    val periodic: PeriodicContentDto?,
    @SerialName("nonPeriodicContent")
    val nonPeriodic: NonPeriodicContentDto?
)