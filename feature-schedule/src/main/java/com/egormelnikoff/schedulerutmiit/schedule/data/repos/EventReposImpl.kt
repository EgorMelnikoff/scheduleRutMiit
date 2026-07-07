package com.egormelnikoff.schedulerutmiit.schedule.data.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.core.database.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toEntity
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventRepos
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao
) : EventRepos {
    override suspend fun save(event: Event) = eventDao.insert(event.toEntity())

    override suspend fun saveWithExtra(
        events: List<Event>,
        eventsExtraData: List<EventExtraData>,
        scheduleId: Long
    ) {
        val eventsExtraData = eventsExtraData.map {
            it.toEntity(
                newScheduleId = scheduleId
            )
        }

        val events = events.map {
            it.toEntity(
                newScheduleId = scheduleId
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

    override fun observeHiddenEvents(scheduleId: Long) = eventDao.observeHiddenEvents(scheduleId).map { it.map { e -> e.toDomain() } }

    override suspend fun getById(id: Long) = eventDao.getById(id).toDomain()

    override suspend fun getCountPerDate(
        date: String,
        scheduleId: Long
    ) = eventDao.getCountPerDate(date, scheduleId)

    override suspend fun getByNameAndType(
        name: String,
        typeName: String?,
        scheduleId: Long
    ) = eventDao.getByNameAndType(name, typeName, scheduleId).map { it.toDomain() }

    override suspend fun update(event: Event) = db.withTransaction {
        eventDao.deleteById(event.id)
        eventDao.insert(event.toEntity())
    }

    override suspend fun updateIsHidden(
        eventId: Long,
        isHidden: Boolean
    ) = eventDao.updateIsHidden(eventId, isHidden)
}