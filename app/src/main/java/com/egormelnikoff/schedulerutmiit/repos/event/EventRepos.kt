package com.egormelnikoff.schedulerutmiit.repos.event

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData

interface EventRepos {
    suspend fun save(event: Event)
    suspend fun saveWithExtra(
        events: List<Event>,
        eventsExtraData: List<EventExtraData>,
        scheduleId: Long
    )

    suspend fun deleteById(eventId: Long)

    suspend fun getCountPerDate(
        date: String,
        scheduleId: Long
    ): Int

    suspend fun update(event: Event)
    suspend fun updateIsHidden(
        eventId: Long,
        isHidden: Boolean
    )
}