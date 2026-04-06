package com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LecturerDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("shortFio")
    val shortFio: String,
    @SerializedName("fullFio")
    val fullFio: String,
    @SerializedName("hint")
    val hint: String
)