package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.v1.data

import kotlinx.serialization.Serializable

@Serializable
internal data class RecurrenceEventExport(
    val interval: Int?
)