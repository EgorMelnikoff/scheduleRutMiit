package com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.dto.remote.timetable.TimetableDto
import com.google.gson.annotations.SerializedName

@Keep
data class ScheduleDto(
    @SerializedName("timetable")
    val timetable: TimetableDto,
    @SerializedName("periodicContent")
    val periodic: PeriodicContentDto?,
    @SerializedName("nonPeriodicContent")
    val nonPeriodic: NonPeriodicContentDto?
)