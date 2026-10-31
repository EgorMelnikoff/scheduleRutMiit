package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model

import kotlinx.serialization.Serializable

@Serializable
data class LecturerImport(
    val id: Int = -1,
    val shortFio: String,
    val fullFio: String,
    val hint: String
)