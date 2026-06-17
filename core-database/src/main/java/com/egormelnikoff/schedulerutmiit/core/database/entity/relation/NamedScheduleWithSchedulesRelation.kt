package com.egormelnikoff.schedulerutmiit.core.database.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.egormelnikoff.schedulerutmiit.core.database.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.ScheduleEntity

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