package com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RoomDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("hint")
    val hint: String
)