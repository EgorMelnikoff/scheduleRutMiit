package com.egormelnikoff.schedulerutmiit.app.dto.remote.institutes

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.GroupDto
import com.google.gson.annotations.SerializedName

@Keep
data class SpecialtyDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("abbreviation")
    val abbreviation: String,
    @SerializedName("groups")
    val groups: List<GroupDto>
)