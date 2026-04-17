package com.egormelnikoff.schedulerutmiit.domain.repos

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.EventExtraData

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
    suspend fun getByNameAndType(
        name: String,
        typeName: String?,
        scheduleId: Long
    ): List<Event>

    suspend fun update(event: Event)
    suspend fun updateIsHidden(
        eventId: Long,
        isHidden: Boolean
    )
}