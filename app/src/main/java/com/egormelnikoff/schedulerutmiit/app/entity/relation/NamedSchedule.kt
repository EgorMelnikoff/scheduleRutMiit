package com.egormelnikoff.schedulerutmiit.app.entity.relation

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity

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