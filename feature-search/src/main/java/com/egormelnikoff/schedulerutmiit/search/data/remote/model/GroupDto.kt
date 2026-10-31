package com.egormelnikoff.schedulerutmiit.search.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GroupDto(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("name")
    val name: String
)

