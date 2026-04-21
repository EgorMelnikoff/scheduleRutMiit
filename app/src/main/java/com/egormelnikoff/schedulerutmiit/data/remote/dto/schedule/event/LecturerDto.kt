package com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
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