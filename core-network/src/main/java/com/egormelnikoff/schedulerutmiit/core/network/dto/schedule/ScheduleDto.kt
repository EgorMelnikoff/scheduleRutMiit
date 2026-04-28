package com.egormelnikoff.schedulerutmiit.core.network.dto.schedule

import com.egormelnikoff.schedulerutmiit.core.network.dto.timetable.TimetableDto
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