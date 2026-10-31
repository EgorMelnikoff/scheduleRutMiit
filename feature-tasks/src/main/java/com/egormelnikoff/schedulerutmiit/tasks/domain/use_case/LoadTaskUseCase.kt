package com.egormelnikoff.schedulerutmiit.tasks.domain.use_case

import com.egormelnikoff.schedulerutmiit.tasks.domain.repos.TaskRepos
import com.egormelnikoff.schedulerutmiit.tasks.ui.edit_task.EditTaskForm
import com.egormelnikoff.schedulerutmiit.tasks.ui.edit_task.EditTaskState
import jakarta.inject.Inject
import java.time.LocalDate

internal class LoadTaskUseCase @Inject constructor(
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