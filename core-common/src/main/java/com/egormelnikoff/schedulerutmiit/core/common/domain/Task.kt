package com.egormelnikoff.schedulerutmiit.core.common.domain

import java.time.LocalDate
import java.time.LocalTime

data class Task(
    val id: Long = 0,
    val text: String,
    val tag: Int,
    val date: LocalDate,
    val time: LocalTime,
    val isCompleted: Boolean
)