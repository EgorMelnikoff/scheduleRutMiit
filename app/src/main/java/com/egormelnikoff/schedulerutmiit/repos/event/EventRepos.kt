package com.egormelnikoff.schedulerutmiit.repos.event

import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData

interface EventRepos {
    suspend fun save(event: EventEntity)
    suspend fun saveWithExtra(
        events: List<EventEntity>,
        eventsExtraData: List<EventExtraData>,
        scheduleId: Long
    )

    suspend fun deleteById(eventId: Long)

    suspend fun getCountPerDate(
        date: String,
        scheduleId: Long
    ): Int

    suspend fun update(event: EventEntity)
    suspend fun updateIsHidden(
        eventId: Long,
        isHidden: Boolean
    )
}