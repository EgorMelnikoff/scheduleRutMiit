package com.egormelnikoff.schedulerutmiit.core.database.entity.relation

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.egormelnikoff.schedulerutmiit.core.database.entity.Event
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.database.entity.ScheduleEntity

@Keep
data class Schedule(
    @Embedded
    val scheduleEntity: ScheduleEntity,
    @Relation(
        entity = Event::class,
        parentColumn = "ScheduleId",
        entityColumn = "eventScheduleId"
    )
    val events: List<Event>,
    @Relation(
        entity = EventExtraData::class,
        parentColumn = "ScheduleId",
        entityColumn = "eventExtraScheduleId"
    )
    val eventsExtraData: List<EventExtraData> = listOf()
)