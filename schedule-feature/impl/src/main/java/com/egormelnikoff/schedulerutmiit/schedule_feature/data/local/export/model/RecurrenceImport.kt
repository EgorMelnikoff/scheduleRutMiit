package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model

import kotlinx.serialization.Serializable

@Serializable
data class RecurrenceImport(
    val interval: Int,
    val firstWeekNumber: Int
)