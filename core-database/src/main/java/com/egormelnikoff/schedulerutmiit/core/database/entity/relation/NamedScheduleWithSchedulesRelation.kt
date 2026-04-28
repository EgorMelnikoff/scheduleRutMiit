package com.egormelnikoff.schedulerutmiit.core.database.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.core.database.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.toDomain

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

fun NamedScheduleWithSchedulesRelation.toDomain() = NamedScheduleWithSchedules(
    namedScheduleEntity.toDomain(), scheduleWithEvents.map { it.toDomain() }
)

//fun NamedScheduleWithSchedules.toRelation() = NamedScheduleWithSchedulesRelation(
//    namedSchedule.toEntity(), scheduleWithEvents.map { it.toRelation() }
//)