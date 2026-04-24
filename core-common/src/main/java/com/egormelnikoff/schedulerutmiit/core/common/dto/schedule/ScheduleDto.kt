package com.egormelnikoff.schedulerutmiit.core.common.dto.schedule

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.core.common.dto.timetable.TimetableDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ScheduleDto(
    @SerialName("timetable")
    val timetable: TimetableDto,
    @SerialName("periodicContent")
    val periodic: PeriodicContentDto?,
    @SerialName("nonPeriodicContent")
    val nonPeriodic: NonPeriodicContentDto?
)