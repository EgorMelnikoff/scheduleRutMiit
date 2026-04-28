package com.egormelnikoff.schedulerutmiit.core.database.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventExtraDataEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.toDomain
import com.egormelnikoff.schedulerutmiit.core.database.entity.toEntity

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

fun ScheduleWithEventsRelation.toDomain() = ScheduleWithEvents(
    scheduleEntity.toDomain(),
    events.map { it.toDomain() },
    eventsExtraData.map { it.toDomain() }
)

fun ScheduleWithEvents.toRelation() = ScheduleWithEventsRelation(
    schedule.toEntity(),
    events.map { it.toEntity() },
    eventsExtraData.map { it.toEntity() }
)