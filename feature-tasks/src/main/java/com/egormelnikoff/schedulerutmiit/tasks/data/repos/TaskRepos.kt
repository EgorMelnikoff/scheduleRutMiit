package com.egormelnikoff.schedulerutmiit.tasks.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

interface TaskRepos {
    suspend fun save(createTask: CreateTask)

    suspend fun deleteById(id: Long)

    suspend fun deleteByDateAndId(id: Long, date: LocalDate)

    fun observeAll(): Flow<Map<LocalDate, List<Task>>>

    suspend fun updateIsCompleted(
        id: Long,
        date: LocalDate,
        isCompleted: Boolean
    )

    suspend fun updateText(
        id: Long,
        text: String
    )

    suspend fun updateTime(
        id: Long,
        time: LocalTime
    )

    suspend fun updateTag(
        id: Long,
        date: LocalDate,
        tag: Int
    )
}