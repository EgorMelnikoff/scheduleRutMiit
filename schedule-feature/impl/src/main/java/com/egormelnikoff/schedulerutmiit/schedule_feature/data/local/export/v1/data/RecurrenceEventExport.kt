package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.v1.data

import kotlinx.serialization.Serializable

@Serializable
data class RecurrenceEventExport(
    val interval: Int?
)