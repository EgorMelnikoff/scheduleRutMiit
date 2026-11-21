package com.egormelnikoff.schedulerutmiit.tasks.ui.edit_task

import com.egormelnikoff.schedulerutmiit.tasks.domain.model.Task

internal sealed interface EditTaskState {
    data object Loading : EditTaskState

    data class Loaded(
        val task: Task,
        val editTaskForm: EditTaskForm
    ) : EditTaskState {
        val isButtonEnabled: Boolean
            get() = (editTaskForm.text != task.text
                    || editTaskForm.tag != task.tag
                    || editTaskForm.time != task.time
                    )
                    && editTaskForm.text.isNotBlank()

    }
}

