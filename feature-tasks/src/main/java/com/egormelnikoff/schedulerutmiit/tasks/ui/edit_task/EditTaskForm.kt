package com.egormelnikoff.schedulerutmiit.tasks.ui.edit_task

import java.time.LocalTime

internal data class EditTaskForm(
    val text: String,
    val tag: Int,
    val time: LocalTime
)