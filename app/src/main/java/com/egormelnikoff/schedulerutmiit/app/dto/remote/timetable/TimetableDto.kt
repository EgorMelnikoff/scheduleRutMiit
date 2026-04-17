package com.egormelnikoff.schedulerutmiit.app.dto.remote.timetable

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.app.serializers.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Keep
@Serializable
data class TimetableDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: TimetableType,
    @SerialName("downloadUrl")
    val downloadUrl: String?,
    @SerialName("startDate")
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    @SerialName("endDate")
    val endDate: LocalDate
)
