package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LecturerDto(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("shortFio")
    val shortFio: String,
    @SerialName("fullFio")
    val fullFio: String,
    @SerialName("hint")
    val hint: String = ""
)