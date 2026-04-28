package com.egormelnikoff.schedulerutmiit.core.network.dto.schedule

import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LecturerDto(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("shortFio")
    val shortFio: String,
    @SerialName("fullFio")
    val fullFio: String,
    @SerialName("hint")
    val hint: String = ""
)

fun LecturerDto.toDomain() = Lecturer(
    id = id,
    shortFio = shortFio,
    fullFio = fullFio,
    hint = hint
)