package com.egormelnikoff.schedulerutmiit.tasks.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.egormelnikoff.schedulerutmiit.tasks.data.local.entity.TaskEntity
import com.egormelnikoff.schedulerutmiit.tasks.data.local.entity.TaskWithCompletionsRelation
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TaskDao {
    @Insert
    suspend fun insert(task: TaskEntity): Long

    @Transaction
    @Query("SELECT * FROM tasks")
    fun observeAll(): Flow<List<TaskWithCompletionsRelation>>

    @Query("DELETE FROM Tasks WHERE TaskId = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM Tasks Where TaskId = :id")
    suspend fun getById(id: Long): TaskEntity

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