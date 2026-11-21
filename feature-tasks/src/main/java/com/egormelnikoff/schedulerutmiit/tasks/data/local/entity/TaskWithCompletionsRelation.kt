package com.egormelnikoff.schedulerutmiit.tasks.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

internal data class TaskWithCompletionsRelation(
    @Embedded
    val task: TaskEntity,

    @Relation(
        parentColumn = "TaskId",
        entityColumn = "taskId"
    )
    val completions: List<TaskCompletionEntity>
)