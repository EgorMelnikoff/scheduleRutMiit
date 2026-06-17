package com.egormelnikoff.schedulerutmiit.core.database.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventExtraDataEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.ScheduleEntity

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