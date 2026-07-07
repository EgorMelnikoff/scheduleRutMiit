package com.egormelnikoff.schedulerutmiit.core.database.entity.embedded

data class RecurrenceEntity(
    val interval: Int,
    val firstWeekNumber: Int
)