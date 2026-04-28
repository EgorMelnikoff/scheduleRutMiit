package com.egormelnikoff.schedulerutmiit.core.network.dto.schedule

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("name")
    val name: String
)

fun GroupDto.toDomain() = Group(
    id = id, name = name
)