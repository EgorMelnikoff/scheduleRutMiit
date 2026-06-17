package com.egormelnikoff.schedulerutmiit.tasks.domain.use_case

import com.egormelnikoff.schedulerutmiit.tasks.data.repos.CreateTask
import com.egormelnikoff.schedulerutmiit.tasks.data.repos.TaskRepos
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

sealed class TaskAction {
    data class Add(
        val createTask: CreateTask
    ) : TaskAction()

    data class UpdateIsCompleted(
        val id: Long,
        val date: LocalDate,
        val isCompleted: Boolean
    ) : TaskAction()

    data class UpdateText(
        val id: Long,
        val text: String
    ) : TaskAction()

    data class UpdateTag(
        val id: Long,
        val date: LocalDate,
        val tag: Int
    ) : TaskAction()

    data class UpdateTime(
        val id: Long,
        val time: LocalTime
    ) : TaskAction()

    data class DeleteById(
        val id: Long
    ) : TaskAction()

    data class DeleteByDateAndId(
        val id: Long,
        val date: LocalDate
    ) : TaskAction()
}

class TaskActionUseCase @Inject constructor(
    private val taskRepos: TaskRepos
) {
    suspend operator fun invoke(
        taskAction: TaskAction
    ) {
        when (taskAction) {
            is TaskAction.Add -> taskRepos.save(taskAction.createTask)
            is TaskAction.UpdateText -> taskRepos.updateText(taskAction.id, taskAction.text)
            is TaskAction.UpdateIsCompleted -> taskRepos.updateIsCompleted(
                taskAction.id,
                taskAction.date,
                taskAction.isCompleted
            )

            is TaskAction.UpdateTag -> taskRepos.updateTag(
                taskAction.id,
                taskAction.date,
                taskAction.tag
            )

            is TaskAction.UpdateTime -> taskRepos.updateTime(
                taskAction.id,
                taskAction.time
            )

            is TaskAction.DeleteById -> taskRepos.deleteById(taskAction.id)

            is TaskAction.DeleteByDateAndId -> taskRepos.deleteByDateAndId(
                taskAction.id,
                taskAction.date
            )
        }
    }
}