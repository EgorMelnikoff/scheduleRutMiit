package com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class GroupDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)