package com.egormelnikoff.schedulerutmiit.tasks.ui.add_task

import java.time.LocalDate
import java.time.LocalTime

internal data class AddTaskForm(
    val text: String = "",
    val tag: Int = 0,
    val time: LocalTime? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
) {
    val isButtonEnabled: Boolean
        get() = text.isNotBlank() && startDate != null && endDate != null && time != null
}