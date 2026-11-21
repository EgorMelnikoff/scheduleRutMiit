package com.egormelnikoff.schedulerutmiit.tasks.domain.repos

import com.egormelnikoff.schedulerutmiit.tasks.domain.model.Task
import com.egormelnikoff.schedulerutmiit.tasks.ui.add_task.AddTaskForm
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

internal interface TaskRepos {
    suspend fun save(addTaskForm: AddTaskForm)

    suspend fun getByIdAndDate(taskId: Long, date: LocalDate): Task

    suspend fun deleteById(id: Long)

    suspend fun deleteByDateAndId(id: Long, date: LocalDate)

    fun observeAll(): Flow<Map<LocalDate, List<Task>>>

    suspend fun updateIsCompleted(
        id: Long,
        date: LocalDate,
        isCompleted: Boolean
    )

    suspend fun updateTask(
        task: Task
    )
}