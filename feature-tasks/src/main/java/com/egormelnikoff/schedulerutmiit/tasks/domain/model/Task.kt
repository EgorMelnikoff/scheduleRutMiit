package com.egormelnikoff.schedulerutmiit.tasks.domain.model

import java.time.LocalDate
import java.time.LocalTime

internal data class Task(
    val id: Long = 0,
    val text: String,
    val tag: Int,
    val date: LocalDate,
    val time: LocalTime,
    val isCompleted: Boolean
)