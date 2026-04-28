package com.egormelnikoff.schedulerutmiit.core.network.dto.institutes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstituteDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("abbreviation")
    val abbreviation: String,
    @SerialName("courses")
    val courses: List<CourseDto>
)