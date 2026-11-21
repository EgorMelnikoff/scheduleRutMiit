package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.v1.data

import kotlinx.serialization.Serializable

@Serializable
data class RecurrenceEventExport(
    val interval: Int?
)