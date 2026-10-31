package com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("name")
    val name: String
)

