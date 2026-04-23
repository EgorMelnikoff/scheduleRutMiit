package com.egormelnikoff.schedulerutmiit.core.database.entity.relation

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.egormelnikoff.schedulerutmiit.core.database.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.ScheduleEntity

@Keep
data class NamedSchedule(
    @Embedded
    val namedScheduleEntity: NamedScheduleEntity,
    @Relation(
        entity = ScheduleEntity::class,
        parentColumn = "NamedScheduleId",
        entityColumn = "namedScheduleId"
    )
    val schedules: List<Schedule>
)