package com.egormelnikoff.schedulerutmiit.tasks.domain.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.core.common.domain.Task
import com.egormelnikoff.schedulerutmiit.core.database.dao.TaskCompletionDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.TaskDao
import com.egormelnikoff.schedulerutmiit.core.database.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskCompletionEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskEntity
import com.egormelnikoff.schedulerutmiit.tasks.data.repos.CreateTask
import com.egormelnikoff.schedulerutmiit.tasks.data.repos.TaskRepos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class TaskReposImpl @Inject constructor(
    val db: AppDatabase,
    val taskDao: TaskDao,
    val taskCompletionDao: TaskCompletionDao
) : TaskRepos {
    override suspend fun save(createTask: CreateTask) = db.withTransaction {
        val taskId = taskDao.insert(
            TaskEntity(
                text = createTask.text
            )
        )

        val dates = generateSequence(createTask.startDate) { date ->
            date.plusDays(1).takeIf { it <= createTask.endDate }
        }.toList()

        val completions = dates.map { date ->
            TaskCompletionEntity(
                taskId = taskId,
                date = date,
                time = createTask.time,
                tag = createTask.tag,
                isCompleted = false
            )
        }

        taskCompletionDao.insertAll(completions)
    }

    override suspend fun deleteById(id: Long) {
        taskDao.deleteById(id)
    }

    override suspend fun deleteByDateAndId(id: Long, date: LocalDate) {
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

    override suspend fun updateText(id: Long, text: String) {
        taskDao.updateText(id, text)
    }

    override suspend fun updateTag(id: Long, date: LocalDate, tag: Int) {
        taskCompletionDao.updateTag(id, date, tag)
    }

    override suspend fun updateIsCompleted(id: Long, date: LocalDate, isCompleted: Boolean) {
        taskCompletionDao.updateIsCompleted(id, date, isCompleted)
    }

    override suspend fun updateTime(id: Long, time: LocalTime) {
        taskCompletionDao.updateTime(id, time)
    }
}