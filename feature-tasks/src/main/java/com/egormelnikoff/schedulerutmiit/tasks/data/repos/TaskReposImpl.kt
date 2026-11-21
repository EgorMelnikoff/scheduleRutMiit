package com.egormelnikoff.schedulerutmiit.tasks.data.repos

import com.egormelnikoff.schedulerutmiit.tasks.data.local.TaskLocal
import com.egormelnikoff.schedulerutmiit.tasks.domain.model.Task
import com.egormelnikoff.schedulerutmiit.tasks.domain.repos.TaskRepos
import com.egormelnikoff.schedulerutmiit.tasks.ui.add_task.AddTaskForm
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

internal class TaskReposImpl @Inject constructor(
    private val taskLocal: TaskLocal
): TaskRepos {
    override suspend fun save(addTaskForm: AddTaskForm) {
        taskLocal.save(addTaskForm)
    }

    override suspend fun getByIdAndDate(
        taskId: Long,
        date: LocalDate
    ) = taskLocal.getByIdAndDate(taskId, date)

    override suspend fun deleteById(id: Long) {
        taskLocal.deleteById(id)
    }

    override suspend fun deleteByDateAndId(id: Long, date: LocalDate) {
        taskLocal.deleteByDateAndId(id, date)
    }

    override fun observeAll(): Flow<Map<LocalDate, List<Task>>> = taskLocal.observeAll()

    override suspend fun updateIsCompleted(
        id: Long,
        date: LocalDate,
        isCompleted: Boolean
    ) {
        taskLocal.updateIsCompleted(id, date, isCompleted)
    }

    override suspend fun updateTask(task: Task) {
        taskLocal.updateTask(task)
    }

}