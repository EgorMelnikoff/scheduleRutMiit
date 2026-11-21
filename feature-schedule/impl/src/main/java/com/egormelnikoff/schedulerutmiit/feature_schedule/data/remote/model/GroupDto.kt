package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("name")
    val name: String
)

