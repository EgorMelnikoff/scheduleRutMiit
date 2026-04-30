package com.egormelnikoff.schedulerutmiit.export.domain.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.core.common.domain.ExportData
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.core.database.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.core.database.entity.toDomain
import com.egormelnikoff.schedulerutmiit.core.database.entity.toEntity
import com.egormelnikoff.schedulerutmiit.export.data.repos.DataRepos
import javax.inject.Inject

class DataReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val namedScheduleDao: NamedScheduleDao,
    private val scheduleDao: ScheduleDao,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao
) : DataRepos {
    override suspend fun getExportData(): ExportData = db.withTransaction {
        return@withTransaction ExportData(
            version = 1,
            namedSchedules = namedScheduleDao.getAll().map { it.toDomain() },
            schedules = scheduleDao.getAll().map { it.toDomain() },
            events = eventDao.getAll().map { it.toDomain() },
            eventsExtraData = eventExtraDao.getAll().map { it.toDomain() }
        )
    }

    override suspend fun importData(data: ExportData) = db.withTransaction {
        when (data.version) {
            1 -> importV1(data)
            else -> error("Unsupported version: ${data.version}")
        }
    }

    private suspend fun importV1(data: ExportData) = db.withTransaction {
        eventExtraDao.deleteAll()
        eventDao.deleteAll()
        scheduleDao.deleteAll()
        namedScheduleDao.deleteAll()

        namedScheduleDao.insertAll(data.namedSchedules.map { it.toEntity() })
        scheduleDao.insertAll(data.schedules.map { it.toEntity() })
        eventDao.insertAll(data.events.map { it.toEntity() })
        eventExtraDao.insertAll(data.eventsExtraData.map { it.toEntity() })
    }
}