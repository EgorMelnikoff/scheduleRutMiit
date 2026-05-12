package com.egormelnikoff.schedulerutmiit.schedule.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toEntity
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventExtraRepos
import java.time.LocalDateTime
import javax.inject.Inject

class EventExtraReposImpl @Inject constructor(
    private val eventExtraDao: EventExtraDao
) : EventExtraRepos {
    override suspend fun save(
        eventExtraData: EventExtraData
    ) = eventExtraDao.insert(eventExtraData.toEntity())


    override suspend fun delete(eventId: Long, dateTime: LocalDateTime?) {
        if (dateTime != null) {
            eventExtraDao.deleteByEventIdAndDateTime(eventId, dateTime)
        } else {
            eventExtraDao.deleteByEventId(eventId)
        }
    }

    override suspend fun getByScheduleId(scheduleId: Long): List<EventExtraData> {
        return eventExtraDao.getByScheduleId(scheduleId).map { it.toDomain() }
    }

    override suspend fun get(
        eventId: Long,
        dateTime: LocalDateTime?
    ): EventExtraData? {
        if (dateTime != null) {
            return eventExtraDao.getByEventIdAndDateTime(eventId, dateTime)?.toDomain()
        }
        return eventExtraDao.getByEventId(eventId)?.toDomain()
    }

    override suspend fun updateComment(
        event: Event,
        dateTime: LocalDateTime?,
        newComment: String
    ) {
        if (dateTime != null) {
            eventExtraDao.updateCommentByEventIdAndDateTime(event.id, dateTime, newComment)
        } else {
            eventExtraDao.updateCommentByEventId(event.id, newComment)
        }
    }

    override suspend fun updateTag(
        event: Event,
        dateTime: LocalDateTime?,
        newTag: Int
    ) {
        if (dateTime != null) {
            eventExtraDao.updateTagByEventIdAndDateTime(event.id, dateTime, newTag)
        } else {
            eventExtraDao.updateTagByEventId(event.id, newTag)
        }
    }
}