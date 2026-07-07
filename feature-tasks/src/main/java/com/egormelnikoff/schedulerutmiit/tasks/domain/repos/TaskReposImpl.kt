package com.egormelnikoff.schedulerutmiit.tasks.domain.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.core.common.domain.Task
import com.egormelnikoff.schedulerutmiit.core.database.dao.TaskCompletionDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.TaskDao
import com.egormelnikoff.schedulerutmiit.core.database.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskCompletionEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskEntity
import com.egormelnikoff.schedulerutmiit.tasks.data.repos.TaskRepos
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.add_task.view_model.state.AddTaskForm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TaskReposImpl @Inject constructor(
    val db: AppDatabase,
    val taskDao: TaskDao,
    val taskCompletionDao: TaskCompletionDao
) : TaskRepos {
    override suspend fun save(addTaskForm: AddTaskForm) = db.withTransaction {
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

    override suspend fun getByIdAndDate(taskId: Long, date: LocalDate): Task {
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

    override suspend fun deleteById(id: Long) {
        taskDao.deleteById(id)
    }

    override suspend fun deleteByDateAndId(id: Long, date: LocalDate) = db.withTransaction {
        taskCompletionDao.deleteByTaskIdAndDate(id, date)
        if (taskCompletionDao.countById(id) == 0) {
            taskDao.deleteById(id)
        }
    }

    override fun observeAll(): Flow<Map<LocalDate, List<Task>>> {
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


    override suspend fun updateTask(task: Task) = db.withTransaction {
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

    override suspend fun updateIsCompleted(id: Long, date: LocalDate, isCompleted: Boolean) {
        taskCompletionDao.updateIsCompleted(id, date, isCompleted)
    }
}