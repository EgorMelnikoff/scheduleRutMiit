package com.egormelnikoff.schedulerutmiit.repos.schedule

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.datasource.local.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.repos.event.EventRepos
import javax.inject.Inject

class ScheduleReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val scheduleDao: ScheduleDao,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao,
    private val eventRepos: EventRepos
) : ScheduleRepos {
    override suspend fun saveAllSchedules(
        namedScheduleId: Long,
        schedules: List<ScheduleFormatted>
    ) = db.withTransaction {
        val schedulesToInsert = schedules.map {
            it.scheduleEntity.copy(namedScheduleId = namedScheduleId)
        }

        val scheduleIds = scheduleDao.insertAll(schedulesToInsert)

        val eventsToInsert = schedules.zip(scheduleIds) { formatted, id ->
            formatted.events.map { event ->
                event.copy(scheduleId = id)
            }
        }.flatten()

        eventDao.insertAll(eventsToInsert)
    }

    override suspend fun save(
        namedScheduleId: Long,
        scheduleFormatted: ScheduleFormatted
    ) = db.withTransaction {
        val scheduleId = scheduleDao.insert(
            scheduleFormatted.scheduleEntity.copy(
                namedScheduleId = namedScheduleId
            )
        )
        eventRepos.saveWithExtra(
            events = scheduleFormatted.events,
            eventsExtraData = scheduleFormatted.eventsExtraData,
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
        scheduleFormatted: ScheduleFormatted
    ) = db.withTransaction {
        eventDao.deleteByScheduleId(scheduleId)
        eventExtraDao.deleteByScheduleId(scheduleId)
        eventRepos.saveWithExtra(
            scheduleFormatted.events,
            scheduleFormatted.eventsExtraData,
            scheduleId
        )
    }

    override suspend fun setDefaultSchedule(
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