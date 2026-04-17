package com.egormelnikoff.schedulerutmiit.data.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.data.local.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.data.local.db.dao.EventDao
import com.egormelnikoff.schedulerutmiit.data.local.db.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.domain.repos.EventRepos
import javax.inject.Inject

class EventReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao
) : EventRepos {
    override suspend fun save(event: Event) = eventDao.insert(event)

    override suspend fun saveWithExtra(
        events: List<Event>,
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

    override suspend fun getByNameAndType(
        name: String,
        typeName: String?,
        scheduleId: Long
    ) = eventDao.getByNameAndType(name, typeName, scheduleId)

    override suspend fun update(event: Event) = db.withTransaction {
        eventDao.deleteById(event.id)
        eventDao.insert(event)
    }

    override suspend fun updateIsHidden(
        eventId: Long,
        isHidden: Boolean
    ) = eventDao.updateIsHidden(eventId, isHidden)
}