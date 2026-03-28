package com.egormelnikoff.schedulerutmiit.repos.event

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.datasource.local.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventExtraDao
import javax.inject.Inject

class EventReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao
) : EventRepos {
    override suspend fun save(event: EventEntity) = eventDao.insert(event)

    override suspend fun saveWithExtra(
        events: List<EventEntity>,
        eventsExtraData: List<EventExtraData>,
        scheduleId: Long
    ) {
        val eventsExtraData = eventsExtraData.map {
            it.copy(
                scheduleId = scheduleId
            )
        }

        val events = events.map {
            it.copy(
                scheduleId = scheduleId
            )
        }

        eventDao.insertAll(events)
        eventExtraDao.insertAll(eventsExtraData)
    }

    override suspend fun deleteById(
        eventId: Long
    ) = db.withTransaction {
        eventDao.deleteById(eventId)
        eventExtraDao.deleteByEventId(eventId)
    }


    override suspend fun getCountPerDate(
        date: String,
        scheduleId: Long
    ) = eventDao.getCountPerDate(date, scheduleId)


    override suspend fun update(event: EventEntity) = db.withTransaction {
        eventDao.deleteById(event.id)
        eventDao.insert(event)
    }

    override suspend fun updateIsHidden(
        eventId: Long,
        isHidden: Boolean
    ) = eventDao.updateIsHidden(eventId, isHidden)
}