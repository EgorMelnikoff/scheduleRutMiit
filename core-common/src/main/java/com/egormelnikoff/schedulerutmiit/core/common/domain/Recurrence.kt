package com.egormelnikoff.schedulerutmiit.core.common.domain

import kotlinx.serialization.Serializable

@Serializable
data class Recurrence(
    val interval: Int,
    val firstWeekNumber: Int = 1
)