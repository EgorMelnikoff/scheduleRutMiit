package com.egormelnikoff.schedulerutmiit.core.database.entity.serializable

import kotlinx.serialization.Serializable

@Serializable
data class RoomEntity(
    val id: Int = -1,
    val name: String,
    val hint: String = ""
)