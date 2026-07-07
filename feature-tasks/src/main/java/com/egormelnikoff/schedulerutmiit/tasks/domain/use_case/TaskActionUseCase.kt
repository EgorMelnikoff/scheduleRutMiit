package com.egormelnikoff.schedulerutmiit.tasks.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Task
import com.egormelnikoff.schedulerutmiit.tasks.data.repos.TaskRepos
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.add_task.view_model.state.AddTaskForm
import java.time.LocalDate
import javax.inject.Inject

sealed interface TaskAction {
    data class Add(
        val addTaskForm: AddTaskForm
    ) : TaskAction

    data class UpdateTask(
        val updatedTask: Task
    ) : TaskAction

    data class UpdateIsCompleted(
        val id: Long,
        val date: LocalDate,
        val isCompleted: Boolean
    ) : TaskAction

    data class DeleteById(
        val id: Long
    ) : TaskAction

    data class DeleteByDateAndId(
        val id: Long,
        val date: LocalDate
    ) : TaskAction
}

class TaskActionUseCase @Inject constructor(
    private val taskRepos: TaskRepos
) {
    suspend operator fun invoke(
        taskAction: TaskAction
    ) {
        when (taskAction) {
            is TaskAction.Add -> taskRepos.save(taskAction.addTaskForm)
            is TaskAction.UpdateIsCompleted -> taskRepos.updateIsCompleted(
                taskAction.id,
                taskAction.date,
                taskAction.isCompleted
            )

            is TaskAction.UpdateTask -> taskRepos.updateTask(taskAction.updatedTask)

            is TaskAction.DeleteById -> taskRepos.deleteById(taskAction.id)

            is TaskAction.DeleteByDateAndId -> taskRepos.deleteByDateAndId(
                taskAction.id,
                taskAction.date
            )
        }
    }
}