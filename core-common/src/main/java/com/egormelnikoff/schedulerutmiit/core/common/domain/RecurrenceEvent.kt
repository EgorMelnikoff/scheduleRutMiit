package com.egormelnikoff.schedulerutmiit.core.common.domain

import kotlinx.serialization.Serializable

@Serializable
data class RecurrenceEvent(
    val frequency: String,
    val interval: Int
) {
}