package com.egormelnikoff.schedulerutmiit.domain.repos

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.EventExtraData

interface EventExtraRepos {
    suspend fun save(
        event: Event,
        tag: Int,
        comment: String
    )

    suspend fun deleteByEventId(eventId: Long)

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