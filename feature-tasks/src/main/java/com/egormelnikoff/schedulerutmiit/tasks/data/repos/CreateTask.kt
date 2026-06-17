package com.egormelnikoff.schedulerutmiit.tasks.data.repos

import java.time.LocalDate
import java.time.LocalTime

data class CreateTask(
    val text: String,
    val tag: Int,
    val time: LocalTime,
    val startDate: LocalDate,
    val endDate: LocalDate,
)