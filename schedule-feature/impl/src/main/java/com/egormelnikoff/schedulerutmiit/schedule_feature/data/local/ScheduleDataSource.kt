package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.mapper.toEntity
import javax.inject.Inject

class ScheduleDataSource @Inject constructor(
    private val db: ScheduleDatabase,
    private val scheduleDao: ScheduleDao,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao,
    private val eventDataSource: EventDataSource
) {
    suspend fun saveAll(
        namedScheduleId: Long,
        scheduleWithEvents: List<ScheduleWithEvents>
    ) = db.withTransaction {
        val schedulesToInsert = scheduleWithEvents.map {
            it.schedule.toEntity(
                newNamedScheduleId = namedScheduleId
            )
        }

        val scheduleIds = scheduleDao.insertAll(schedulesToInsert)

        val eventsToInsert = scheduleWithEvents.zip(scheduleIds) { formatted, id ->
            formatted.events.map { event ->
                event.toEntity(newScheduleId = id)
            }
        }.flatten()

        eventDao.insertAll(eventsToInsert)
    }

    suspend fun save(
        namedScheduleId: Long,
        scheduleWithEvents: ScheduleWithEvents
    ) = db.withTransaction {
        val scheduleId = scheduleDao.insert(
            scheduleWithEvents.schedule.toEntity(
                newNamedScheduleId = namedScheduleId
            )
        )
        eventDataSource.saveWithExtra(
            events = scheduleWithEvents.events,
            eventsExtraData = scheduleWithEvents.eventsExtraData,
            scheduleId = scheduleId
        )

        return@withTransaction scheduleId
    }


    suspend fun getById(id: Long) = scheduleDao.getById(id).toDomain()


    suspend fun deleteById(
        scheduleId: Long
    ) = db.withTransaction {
        scheduleDao.deleteById(scheduleId)
        eventDao.deleteByScheduleId(scheduleId)
        eventExtraDao.deleteByScheduleId(scheduleId)
    }

    suspend fun updateEvents(
        scheduleId: Long,
        scheduleWithEvents: ScheduleWithEvents
    ) = db.withTransaction {
        eventDao.deleteByScheduleId(scheduleId)
        eventExtraDao.deleteByScheduleId(scheduleId)
        eventDataSource.saveWithExtra(
            scheduleWithEvents.events,
            scheduleWithEvents.eventsExtraData,
            scheduleId
        )
    }

    suspend fun setDefault(
        namedScheduleId: Long,
        scheduleId: Long
    ) = db.withTransaction {
        scheduleDao.setDefault(scheduleId)
        scheduleDao.setNonDefault(
            namedScheduleId = namedScheduleId,
            scheduleId = scheduleId
        )
    }
}