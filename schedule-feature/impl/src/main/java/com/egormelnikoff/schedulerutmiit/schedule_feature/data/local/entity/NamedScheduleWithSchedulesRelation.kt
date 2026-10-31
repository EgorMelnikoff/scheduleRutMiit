package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class NamedScheduleWithSchedulesRelation(
    @Embedded
    val namedScheduleEntity: NamedScheduleEntity,
    @Relation(
        entity = ScheduleEntity::class,
        parentColumn = "NamedScheduleId",
        entityColumn = "namedScheduleId"
    )
    val scheduleWithEvents: List<ScheduleWithEventsRelation>
)