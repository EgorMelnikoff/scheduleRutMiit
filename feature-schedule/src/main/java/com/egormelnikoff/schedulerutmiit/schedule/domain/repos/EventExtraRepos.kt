package com.egormelnikoff.schedulerutmiit.schedule.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import java.time.LocalDate

interface EventExtraRepos {
    suspend fun save(
        eventExtraData: EventExtraData
    )

    suspend fun delete(eventId: Long, date: LocalDate?)

    suspend fun getByScheduleId(scheduleId: Long): List<EventExtraData>

    suspend fun get(eventId: Long, date: LocalDate?): EventExtraData?

    suspend fun updateComment(
        event: Event,
        date: LocalDate?,
        newComment: String
    )
    suspend fun updateTag(
        event: Event,
        date: LocalDate?,
        newTag: Int
    )
}