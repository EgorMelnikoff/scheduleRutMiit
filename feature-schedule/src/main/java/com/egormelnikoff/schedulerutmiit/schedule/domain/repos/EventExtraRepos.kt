package com.egormelnikoff.schedulerutmiit.schedule.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import java.time.LocalDateTime

interface EventExtraRepos {
    suspend fun save(
        eventExtraData: EventExtraData
    )

    suspend fun delete(eventId: Long, dateTime: LocalDateTime?)

    suspend fun getByScheduleId(scheduleId: Long): List<EventExtraData>

    suspend fun get(eventId: Long, dateTime: LocalDateTime?): EventExtraData?

    suspend fun updateComment(
        event: Event,
        dateTime: LocalDateTime?,
        newComment: String
    )
    suspend fun updateTag(
        event: Event,
        dateTime: LocalDateTime?,
        newTag: Int
    )
}