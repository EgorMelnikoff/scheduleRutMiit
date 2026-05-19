package com.egormelnikoff.schedulerutmiit.core.database.entity

import kotlinx.serialization.Serializable

@Serializable
data class GroupEntity(
    val id: Int = -1,
    val name: String
)

