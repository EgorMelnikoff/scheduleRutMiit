package com.egormelnikoff.schedulerutmiit.repos.event_extra

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData

interface EventExtraRepos {
    suspend fun save(
        event: Event,
        tag: Int,
        comment: String
    )

    suspend fun deleteByEvent(event: Event)

    suspend fun getByEventId(eventId: Long): EventExtraData?

    suspend fun updateComment(
        event: Event,
        newComment: String
    )
    suspend fun updateTag(
        event: Event,
        newTag: Int
    )
}