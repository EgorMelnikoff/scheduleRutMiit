package com.egormelnikoff.schedulerutmiit.search.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class InstituteDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("abbreviation")
    val abbreviation: String,
    @SerialName("courses")
    val courses: List<CourseDto>
)