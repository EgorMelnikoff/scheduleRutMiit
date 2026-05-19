package com.egormelnikoff.schedulerutmiit.core.database.entity

data class RecurrenceEntity(
    val interval: Int,
    val currentNumber: Int,
    val firstWeekNumber: Int
)