package com.egormelnikoff.schedulerutmiit.core.network.dto.institutes

import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.GroupDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecialtyDto(
    @SerialName("name")
    val name: String,
    @SerialName("abbreviation")
    val abbreviation: String,
    @SerialName("groups")
    val groups: List<GroupDto>
)