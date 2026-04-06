package com.egormelnikoff.schedulerutmiit.app.dto.remote.timetable

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

@Keep
data class TimetableDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: TimetableType,
    @SerializedName("downloadUrl")
    val downloadUrl: String?,
    @SerializedName("startDate")
    val startDate: LocalDate,
    @SerializedName("endDate")
    val endDate: LocalDate
)
