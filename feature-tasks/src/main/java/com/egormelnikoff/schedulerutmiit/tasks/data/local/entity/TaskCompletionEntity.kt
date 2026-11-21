package com.egormelnikoff.schedulerutmiit.tasks.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "TaskCompletions",
    primaryKeys = ["taskId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["TaskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class TaskCompletionEntity(
    val taskId: Long,
    val date: LocalDate,
    val time: LocalTime,
    val tag: Int,
    val isCompleted: Boolean
)