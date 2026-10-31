package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model

data class Recurrence(
    val interval: Int,
    val firstWeekNumber: Int
)