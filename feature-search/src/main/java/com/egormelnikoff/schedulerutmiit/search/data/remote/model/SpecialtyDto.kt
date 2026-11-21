package com.egormelnikoff.schedulerutmiit.search.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SpecialtyDto(
    @SerialName("name")
    val name: String,
    @SerialName("abbreviation")
    val abbreviation: String,
    @SerialName("groups")
    val groups: List<GroupDto>
)