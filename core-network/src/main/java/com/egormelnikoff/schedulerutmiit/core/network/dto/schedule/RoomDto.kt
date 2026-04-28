package com.egormelnikoff.schedulerutmiit.core.network.dto.schedule

import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomDto(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("name")
    val name: String,
    @SerialName("hint")
    val hint: String = ""
)

fun RoomDto.toDomain() = Room(
    id = id,
    name = name,
    hint = hint
)