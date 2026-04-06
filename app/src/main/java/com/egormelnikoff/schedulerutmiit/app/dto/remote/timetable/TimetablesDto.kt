package com.egormelnikoff.schedulerutmiit.app.dto.remote.timetable

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TimetablesDto(
    @SerializedName("timetables")
    val timetables: List<TimetableDto>
)