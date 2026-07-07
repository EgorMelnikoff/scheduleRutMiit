package com.egormelnikoff.schedulerutmiit.tasks.domain.use_case

import com.egormelnikoff.schedulerutmiit.tasks.data.repos.TaskRepos
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task.view_model.state.EditTaskForm
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task.view_model.state.EditTaskState
import jakarta.inject.Inject
import java.time.LocalDate

class LoadTaskUseCase @Inject constructor(
    private val taskRepos: TaskRepos
) {
    suspend operator fun invoke(taskId: Long, date: LocalDate): EditTaskState.Loaded {
        val task = taskRepos.getByIdAndDate(taskId, date)
        return EditTaskState.Loaded(
            task = task,
            editTaskForm = EditTaskForm(
                text = task.text,
                tag = task.tag,
                time = task.time
            )
        )
    }
}