package com.egormelnikoff.schedulerutmiit.core.common.dto

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RoomDto(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("name")
    val name: String,
    @SerialName("hint")
    val hint: String = ""
)