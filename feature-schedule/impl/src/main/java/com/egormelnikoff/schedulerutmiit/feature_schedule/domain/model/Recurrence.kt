package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model

internal data class Recurrence(
    val interval: Int,
    val firstWeekNumber: Int
)