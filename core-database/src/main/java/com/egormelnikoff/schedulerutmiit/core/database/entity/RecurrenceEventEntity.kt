package com.egormelnikoff.schedulerutmiit.core.database.entity

import com.egormelnikoff.schedulerutmiit.core.common.domain.RecurrenceEvent

data class RecurrenceEventEntity(
    val frequency: String,
    val interval: Int
)

fun RecurrenceEventEntity.toDomain() = RecurrenceEvent(
    frequency, interval
)

fun RecurrenceEvent.toEntity() = RecurrenceEventEntity(
    frequency, interval
)