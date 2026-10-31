package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
data class GroupEntity(
    val id: Int = -1,
    val name: String
)

