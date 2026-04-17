package com.egormelnikoff.schedulerutmiit.app.dto.remote.institutes

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CourseDto(
    @SerialName("course")
    val course: String,
    @SerialName("specialties")
    val specialties: List<SpecialtyDto>
)