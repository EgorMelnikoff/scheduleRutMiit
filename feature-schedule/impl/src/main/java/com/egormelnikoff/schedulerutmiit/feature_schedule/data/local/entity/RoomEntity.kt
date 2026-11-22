package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class RoomEntity(
    val id: Int = -1,
    val name: String,
    val hint: String = ""
)