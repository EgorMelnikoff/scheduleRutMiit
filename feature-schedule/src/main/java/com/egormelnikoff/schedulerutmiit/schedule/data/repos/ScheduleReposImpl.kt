package com.egormelnikoff.schedulerutmiit.schedule.data.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.core.database.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toEntity
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import javax.inject.Inject

class ScheduleReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val scheduleDao: ScheduleDao,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao,
    private val eventRepos: EventRepos
) : ScheduleRepos {
    override suspend fun saveAll(
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

    override suspend fun save(
        namedScheduleId: Long,
        scheduleWithEvents: ScheduleWithEvents
    ) = db.withTransaction {
        val scheduleId = scheduleDao.insert(
            scheduleWithEvents.schedule.toEntity(
                newNamedScheduleId = namedScheduleId
            )
        )
        eventRepos.saveWithExtra(
            events = scheduleWithEvents.events,
            eventsExtraData = scheduleWithEvents.eventsExtraData,
            scheduleId = scheduleId
        )

        return@withTransaction scheduleId
    }

    override suspend fun deleteById(
        scheduleId: Long
    ) = db.withTransaction {
        scheduleDao.deleteById(scheduleId)
        eventDao.deleteByScheduleId(scheduleId)
        eventExtraDao.deleteByScheduleId(scheduleId)
    }

    override suspend fun updateEvents(
        scheduleId: Long,
        scheduleWithEvents: ScheduleWithEvents
    ) = db.withTransaction {
        eventDao.deleteByScheduleId(scheduleId)
        eventExtraDao.deleteByScheduleId(scheduleId)
        eventRepos.saveWithExtra(
            scheduleWithEvents.events,
            scheduleWithEvents.eventsExtraData,
            scheduleId
        )
    }

    override suspend fun setDefault(
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