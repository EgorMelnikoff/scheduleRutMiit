package com.egormelnikoff.schedulerutmiit.repos.event_extra

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventExtraDao
import javax.inject.Inject

class EventExtraReposImpl @Inject constructor(
    private val eventExtraDao: EventExtraDao
) : EventExtraRepos {
    override suspend fun save(
        event: Event,
        tag: Int,
        comment: String
    ) = eventExtraDao.insert(
        EventExtraData(
            id = event.id,
            scheduleId = event.scheduleId,
            eventName = event.name,
            eventStartDatetime = event.startDatetime,
            comment = comment,
            tag = tag
        )
    )


    override suspend fun deleteByEventId(eventId: Long) = eventExtraDao.deleteByEventId(eventId)

    override suspend fun getByEventId(
        eventId: Long
    ) = eventExtraDao.getByEventId(eventId)

    override suspend fun updateComment(
        event: Event,
        newComment: String
    ) = eventExtraDao.updateComment(event.scheduleId, event.id, newComment)

    override suspend fun updateTag(
        event: Event,
        newTag: Int
    ) = eventExtraDao.updateTag(event.scheduleId, event.id, newTag)
}