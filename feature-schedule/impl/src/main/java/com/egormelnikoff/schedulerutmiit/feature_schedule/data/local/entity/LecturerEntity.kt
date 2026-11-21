package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
data class LecturerEntity(
    val id: Int = -1,
    val shortFio: String,
    val fullFio: String,
    val hint: String = ""
)

