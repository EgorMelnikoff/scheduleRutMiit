package com.egormelnikoff.schedulerutmiit.app.dto.remote.institutes

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.GroupDto
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
    val groups: List<GroupDto>
)