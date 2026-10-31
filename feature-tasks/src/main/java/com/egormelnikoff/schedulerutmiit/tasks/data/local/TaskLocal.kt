package com.egormelnikoff.schedulerutmiit.tasks.data.local

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.tasks.data.local.dao.TaskCompletionDao
import com.egormelnikoff.schedulerutmiit.tasks.data.local.dao.TaskDao
import com.egormelnikoff.schedulerutmiit.tasks.data.local.entity.TaskCompletionEntity
import com.egormelnikoff.schedulerutmiit.tasks.data.local.entity.TaskEntity
import com.egormelnikoff.schedulerutmiit.tasks.domain.model.Task
import com.egormelnikoff.schedulerutmiit.tasks.ui.add_task.AddTaskForm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal class TaskLocal @Inject constructor(
    val db: TaskDatabase,
    val taskDao: TaskDao,
    val taskCompletionDao: TaskCompletionDao
) {
    suspend fun save(addTaskForm: AddTaskForm) = db.withTransaction {
        val taskId = taskDao.insert(
            TaskEntity(
                text = addTaskForm.text
            )
        )

        val dates = generateSequence(addTaskForm.startDate) { date ->
            date.plusDays(1).takeIf { it <= addTaskForm.endDate }
        }.toList()

        addTaskForm.time?.let {
            val completions = dates.map { date ->
                TaskCompletionEntity(
                    taskId = taskId,
                    date = date,
                    time = addTaskForm.time,
                    tag = addTaskForm.tag,
                    isCompleted = false
                )
            }

            taskCompletionDao.insertAll(completions)
        }

        return@withTransaction
    }

    suspend fun getByIdAndDate(taskId: Long, date: LocalDate): Task {
        val taskEntity = taskDao.getById(taskId)
        val taskCompletionEntity = taskCompletionDao.getByTaskIdAndDate(taskId, date)
        return Task(
            id = taskEntity.id,
            text = taskEntity.text,
            date = taskCompletionEntity.date,
            time = taskCompletionEntity.time,
            tag = taskCompletionEntity.tag,
            isCompleted = taskCompletionEntity.isCompleted
        )
    }

    suspend fun deleteById(id: Long) {
        taskDao.deleteById(id)
    }

    suspend fun deleteByDateAndId(id: Long, date: LocalDate) = db.withTransaction {
        taskCompletionDao.deleteByTaskIdAndDate(id, date)
        if (taskCompletionDao.countById(id) == 0) {
            taskDao.deleteById(id)
        }
    }

    fun observeAll(): Flow<Map<LocalDate, List<Task>>> {
        return taskDao.observeAll().map { list ->
            list
                .flatMap { relation ->
                    relation.completions.map { completion ->
                        completion.date to Task(
                            id = relation.task.id,
                            text = relation.task.text,
                            tag = completion.tag,
                            date = completion.date,
                            time = completion.time,
                            isCompleted = completion.isCompleted
                        )
                    }
                }
                .groupBy(
                    keySelector = { it.first },
                    valueTransform = { it.second }
                )
        }
    }


    suspend fun updateTask(task: Task) = db.withTransaction {
        taskDao.updateText(task.id, task.text)
        taskCompletionDao.deleteByTaskIdAndDate(task.id, task.date)
        taskCompletionDao.insert(
            TaskCompletionEntity(
                taskId = task.id,
                date = task.date,
                time = task.time,
                tag = task.tag,
                isCompleted = task.isCompleted
            )
        )
    }

    suspend fun updateIsCompleted(id: Long, date: LocalDate, isCompleted: Boolean) {
        taskCompletionDao.updateIsCompleted(id, date, isCompleted)
    }
}