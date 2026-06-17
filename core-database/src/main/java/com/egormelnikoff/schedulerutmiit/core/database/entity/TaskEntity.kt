package com.egormelnikoff.schedulerutmiit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "Tasks")
data class TaskEntity(
    @ColumnInfo("TaskId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String
)