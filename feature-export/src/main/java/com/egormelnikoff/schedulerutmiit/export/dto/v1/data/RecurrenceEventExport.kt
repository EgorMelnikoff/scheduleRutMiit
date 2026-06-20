package com.egormelnikoff.schedulerutmiit.export.dto.v1.data

import kotlinx.serialization.Serializable

@Serializable
data class RecurrenceEventExport(
    val interval: Int?
)