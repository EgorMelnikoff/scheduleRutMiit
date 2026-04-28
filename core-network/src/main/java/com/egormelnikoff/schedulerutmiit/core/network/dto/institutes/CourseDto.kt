package com.egormelnikoff.schedulerutmiit.core.network.dto.institutes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    @SerialName("course")
    val course: String,
    @SerialName("specialties")
    val specialties: List<SpecialtyDto>
)