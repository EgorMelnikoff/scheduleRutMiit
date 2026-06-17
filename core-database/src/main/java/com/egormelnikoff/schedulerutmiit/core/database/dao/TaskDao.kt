package com.egormelnikoff.schedulerutmiit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.relation.TaskWithCompletionsRelation
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: TaskEntity): Long

    @Query("SELECT * FROM tasks")
    fun observeAll(): Flow<List<TaskWithCompletionsRelation>>

    @Query("DELETE FROM Tasks WHERE TaskId = :id")
    suspend fun deleteById(id: Long)


    @Query("SELECT * FROM Tasks Where TaskId = :id")
    suspend fun getById(id: Long): TaskEntity?

    @Query("UPDATE TaskCompletions SET isCompleted = :isCompleted WHERE TaskId = :id")
    suspend fun updateIsCompleted(
        id: Long,
        isCompleted: Boolean
    )

    @Query("UPDATE Tasks SET text = :text WHERE TaskId = :id")
    suspend fun updateText(
        id: Long,
        text: String
    )
}