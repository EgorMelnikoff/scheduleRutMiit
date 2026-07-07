package com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task.view_model.state

import java.time.LocalTime

data class EditTaskForm(
    val text: String,
    val tag: Int,
    val time: LocalTime
)