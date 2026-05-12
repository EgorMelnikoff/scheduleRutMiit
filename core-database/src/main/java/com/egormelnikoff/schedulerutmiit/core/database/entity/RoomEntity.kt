package com.egormelnikoff.schedulerutmiit.core.database.entity

import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomEntity(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("name")
    val name: String,
    @SerialName("hint")
    val hint: String = ""
)