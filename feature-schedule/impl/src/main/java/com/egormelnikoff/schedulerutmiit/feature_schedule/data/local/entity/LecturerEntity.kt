package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class LecturerEntity(
    val id: Int = -1,
    val shortFio: String,
    val fullFio: String,
    val hint: String = ""
)

