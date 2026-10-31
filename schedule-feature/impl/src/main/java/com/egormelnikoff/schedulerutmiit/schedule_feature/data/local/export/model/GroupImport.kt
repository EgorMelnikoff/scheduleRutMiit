package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupImport(
    val id: Int = -1,
    val name: String
)