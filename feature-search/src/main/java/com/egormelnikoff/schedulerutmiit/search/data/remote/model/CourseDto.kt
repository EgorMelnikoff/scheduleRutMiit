package com.egormelnikoff.schedulerutmiit.search.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CourseDto(
    @SerialName("course")
    val course: String,
    @SerialName("specialties")
    val specialties: List<SpecialtyDto>
)