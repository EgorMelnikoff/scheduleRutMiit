package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupImport(
    val id: Int = -1,
    val name: String
)