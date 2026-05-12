package com.egormelnikoff.schedulerutmiit.core.database.entity

import com.egormelnikoff.schedulerutmiit.core.common.domain.Recurrence

data class RecurrenceEntity(
    val interval: Int,
    val currentNumber: Int,
    val firstWeekNumber: Int
)