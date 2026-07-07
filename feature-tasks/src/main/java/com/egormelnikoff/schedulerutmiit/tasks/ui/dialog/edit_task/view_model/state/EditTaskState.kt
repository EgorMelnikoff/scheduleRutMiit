package com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.domain.Task

sealed interface EditTaskState {
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

