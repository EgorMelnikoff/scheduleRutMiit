package com.egormelnikoff.schedulerutmiit.core.common.dto.schedule

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