package com.egormelnikoff.schedulerutmiit.app.dto.remote.institutes

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class InstituteDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("abbreviation")
    val abbreviation: String,
    @SerializedName("courses")
    val courses: List<CourseDto>
)