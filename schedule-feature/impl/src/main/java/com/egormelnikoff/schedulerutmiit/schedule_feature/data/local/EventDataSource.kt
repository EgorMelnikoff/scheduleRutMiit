package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Event
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.mapper.toEntity
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventDataSource @Inject constructor(
    private val db: ScheduleDatabase,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao
) {
    suspend fun save(event: Event) = eventDao.insert(event.toEntity())

    suspend fun saveWithExtra(
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

    suspend fun deleteById(
        eventId: Long
    ) = db.withTransaction {
        eventDao.deleteById(eventId)
        eventExtraDao.deleteByEventId(eventId)
    }

    fun observeHiddenEvents(scheduleId: Long) = eventDao.observeHiddenEvents(scheduleId).map { it.map { e -> e.toDomain() } }

    suspend fun getById(id: Long) = eventDao.getById(id).toDomain()

    suspend fun getCountPerDate(
        date: String,
        scheduleId: Long
    ) = eventDao.getCountPerDate(date, scheduleId)

    suspend fun getByNameAndType(
        name: String,
        typeName: String?,
        scheduleId: Long
    ) = eventDao.getByNameAndType(name, typeName, scheduleId).map { it.toDomain() }

    suspend fun update(event: Event) = db.withTransaction {
        eventDao.deleteById(event.id)
        eventDao.insert(event.toEntity())
    }

    suspend fun updateIsHidden(
        eventId: Long,
        isHidden: Boolean
    ) = eventDao.updateIsHidden(eventId, isHidden)
}