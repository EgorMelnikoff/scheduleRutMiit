package com.egormelnikoff.schedulerutmiit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskCompletionEntity
import java.time.LocalDate
import java.time.LocalTime

@Dao
interface TaskCompletionDao {
    @Insert
    suspend fun insertAll(
        completions: List<TaskCompletionEntity>
    )

    @Query(" SELECT COUNT(*) FROM TaskCompletions WHERE taskId = :taskId ")
    suspend fun countById(taskId: Long): Int

    @Query("DELETE FROM TaskCompletions WHERE taskId = :taskId AND date = :date")
    suspend fun deleteByTaskIdAndDate(taskId: Long, date: LocalDate)


    @Query("UPDATE TaskCompletions SET isCompleted = :isCompleted WHERE taskId = :id AND date = :date")
    suspend fun updateIsCompleted(
        id: Long,
        date: LocalDate,
        isCompleted: Boolean
    )

    @Query("UPDATE TaskCompletions SET time = :time WHERE taskId = :id")
    suspend fun updateTime(
        id: Long,
        time: LocalTime
    )

    @Query("UPDATE TaskCompletions SET tag = :tag WHERE TaskId = :id AND date = :date")
    suspend fun updateTag(
        id: Long,
        date: LocalDate,
        tag: Int
    )
}