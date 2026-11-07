package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model

import kotlinx.serialization.Serializable

@Serializable
data class RecurrenceImport(
    val interval: Int,
    val firstWeekNumber: Int
)