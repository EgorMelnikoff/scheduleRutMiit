package com.egormelnikoff.schedulerutmiit.app.dto.remote.institutes

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CourseDto(
    @SerializedName("course")
    val course: String,
    @SerializedName("specialties")
    val specialties: List<SpecialtyDto>
)