package com.egormelnikoff.schedulerutmiit.data.remote.dto.institutes

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SpecialtyDto(
    @SerialName("name")
    val name: String,
    @SerialName("abbreviation")
    val abbreviation: String,
    @SerialName("groups")
    val groups: List<com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.GroupDto>
)