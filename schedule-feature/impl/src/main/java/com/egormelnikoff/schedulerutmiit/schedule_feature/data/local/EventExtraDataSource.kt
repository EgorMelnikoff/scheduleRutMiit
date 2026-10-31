package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local

import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Event
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.mapper.toEntity
import java.time.LocalDate
import javax.inject.Inject

class EventExtraDataSource @Inject constructor(
    private val eventExtraDao: EventExtraDao
) {
    suspend fun save(
        eventExtraData: EventExtraData
    ) = eventExtraDao.insert(eventExtraData.toEntity())


    suspend fun delete(eventId: Long, date: LocalDate?) {
        if (date != null) {
            eventExtraDao.deleteByEventIdAndDate(eventId, date)
        } else {
            eventExtraDao.deleteByEventId(eventId)
        }
    }

    suspend fun getByScheduleId(scheduleId: Long): List<EventExtraData> {
        return eventExtraDao.getByScheduleId(scheduleId).map { it.toDomain() }
    }

    suspend fun get(
        eventId: Long,
        date: LocalDate?
    ): EventExtraData? {
        if (date != null) {
            return eventExtraDao.getByEventIdAndDate(eventId, date)?.toDomain()
        }
        return eventExtraDao.getByEventId(eventId)?.toDomain()
    }

    suspend fun updateComment(
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

    suspend fun updateTag(
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