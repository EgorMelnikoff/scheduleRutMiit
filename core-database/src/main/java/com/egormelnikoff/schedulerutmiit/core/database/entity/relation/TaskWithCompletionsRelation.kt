package com.egormelnikoff.schedulerutmiit.core.database.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskCompletionEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskEntity

data class TaskWithCompletionsRelation(
    @Embedded
    val task: TaskEntity,

    @Relation(
        parentColumn = "TaskId",
        entityColumn = "taskId"
    )
    val completions: List<TaskCompletionEntity>
)