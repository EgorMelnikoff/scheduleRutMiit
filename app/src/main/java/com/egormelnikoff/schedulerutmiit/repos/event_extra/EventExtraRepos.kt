package com.egormelnikoff.schedulerutmiit.repos.event_extra

import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData

interface EventExtraRepos {
    suspend fun save(
        event: EventEntity,
        tag: Int,
        comment: String
    )

    suspend fun deleteByEvent(event: EventEntity)

    suspend fun getByEventId(eventId: Long): EventExtraData?

    suspend fun updateComment(
        event: EventEntity,
        newComment: String
    )
    suspend fun updateTag(
        event: EventEntity,
        newTag: Int
    )
}