package com.egormelnikoff.schedulerutmiit.tasks.domain.model

import java.time.LocalDate
import java.time.LocalTime

internal data class CreateTask(
    val text: String,
    val tag: Int,
    val time: LocalTime,
    val startDate: LocalDate,
    val endDate: LocalDate,
)