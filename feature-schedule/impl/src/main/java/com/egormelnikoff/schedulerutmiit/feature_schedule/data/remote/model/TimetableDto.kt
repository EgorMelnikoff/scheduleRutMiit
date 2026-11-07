package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateSerializer
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.TimetableType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

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
