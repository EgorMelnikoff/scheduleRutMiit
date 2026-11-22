package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class GroupEntity(
    val id: Int = -1,
    val name: String
)

