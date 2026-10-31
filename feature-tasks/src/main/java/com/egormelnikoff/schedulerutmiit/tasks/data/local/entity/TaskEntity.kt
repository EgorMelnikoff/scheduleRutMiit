package com.egormelnikoff.schedulerutmiit.tasks.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Tasks"
)
internal data class TaskEntity(
    @ColumnInfo("TaskId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String
)