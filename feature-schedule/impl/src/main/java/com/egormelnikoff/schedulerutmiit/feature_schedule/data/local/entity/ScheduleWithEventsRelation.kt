package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ScheduleWithEventsRelation(
    @Embedded
    val scheduleEntity: ScheduleEntity,
    @Relation(
        entity = EventEntity::class,
        parentColumn = "ScheduleId",
        entityColumn = "eventScheduleId"
    )
    val events: List<EventEntity>,
    @Relation(
        entity = EventExtraDataEntity::class,
        parentColumn = "ScheduleId",
        entityColumn = "eventExtraScheduleId"
    )
    val eventsExtraData: List<EventExtraDataEntity> = listOf()
)