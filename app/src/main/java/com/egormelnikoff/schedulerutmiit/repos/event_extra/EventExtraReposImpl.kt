package com.egormelnikoff.schedulerutmiit.repos.event_extra

import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventExtraDao
import javax.inject.Inject

class EventExtraReposImpl @Inject constructor(
    private val eventExtraDao: EventExtraDao
) : EventExtraRepos {
    override suspend fun save(
        event: EventEntity,
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


    override suspend fun deleteByEvent(event: EventEntity) = eventExtraDao.deleteByEventId(event.id)

    override suspend fun getByEventId(
        eventId: Long
    ) = eventExtraDao.getByEventId(eventId)

    override suspend fun updateComment(
        event: EventEntity,
        newComment: String
    ) = eventExtraDao.updateComment(event.scheduleId, event.id, newComment)

    override suspend fun updateTag(
        event: EventEntity,
        newTag: Int
    ) = eventExtraDao.updateTag(event.scheduleId, event.id, newTag)
}