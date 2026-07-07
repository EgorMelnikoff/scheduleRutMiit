package com.egormelnikoff.schedulerutmiit.schedule.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toEntity
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventExtraRepos
import java.time.LocalDate
import javax.inject.Inject

class EventExtraReposImpl @Inject constructor(
    private val eventExtraDao: EventExtraDao
) : EventExtraRepos {
    override suspend fun save(
        eventExtraData: EventExtraData
    ) = eventExtraDao.insert(eventExtraData.toEntity())


    override suspend fun delete(eventId: Long, date: LocalDate?) {
        if (date != null) {
            eventExtraDao.deleteByEventIdAndDate(eventId, date)
        } else {
            eventExtraDao.deleteByEventId(eventId)
        }
    }

    override suspend fun getByScheduleId(scheduleId: Long): List<EventExtraData> {
        return eventExtraDao.getByScheduleId(scheduleId).map { it.toDomain() }
    }

    override suspend fun get(
        eventId: Long,
        date: LocalDate?
    ): EventExtraData? {
        if (date != null) {
            return eventExtraDao.getByEventIdAndDate(eventId, date)?.toDomain()
        }
        return eventExtraDao.getByEventId(eventId)?.toDomain()
    }

    override suspend fun updateComment(
        event: Event,
        date: LocalDate?,
        newComment: String
    ) {
        if (date != null) {
            eventExtraDao.updateCommentByEventIdAndDateTime(event.id, date, newComment)
        } else {
            eventExtraDao.updateCommentByEventId(event.id, newComment)
        }
    }

    override suspend fun updateTag(
        event: Event,
        date: LocalDate?,
        newTag: Int
    ) {
        if (date != null) {
            eventExtraDao.updateTagByEventIdAndDate(event.id, date, newTag)
        } else {
            eventExtraDao.updateTagByEventId(event.id, newTag)
        }
    }
}