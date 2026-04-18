package com.egormelnikoff.schedulerutmiit.data.repos

import com.egormelnikoff.schedulerutmiit.data.local.db.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.domain.repos.EventExtraRepos
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
            eventId = event.id,
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