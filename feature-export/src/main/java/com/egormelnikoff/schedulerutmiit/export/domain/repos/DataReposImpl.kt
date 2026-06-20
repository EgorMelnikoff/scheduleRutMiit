package com.egormelnikoff.schedulerutmiit.export.domain.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.core.database.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toEntity
import com.egormelnikoff.schedulerutmiit.export.data.repos.DataRepos
import com.egormelnikoff.schedulerutmiit.export.dto.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.export.dto.v2.ExportDataV2
import javax.inject.Inject

class DataReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val namedScheduleDao: NamedScheduleDao,
    private val scheduleDao: ScheduleDao,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao
) : DataRepos {
    override suspend fun getExportData(): ExportDataV2 = db.withTransaction {
        return@withTransaction ExportDataV2(
            importSchedulePayload = ImportSchedulePayload(
                namedSchedules = namedScheduleDao.getAll().map { it.toDomain() },
                schedules = scheduleDao.getAll().map { it.toDomain() },
                events = eventDao.getAll().map { it.toDomain() },
                eventsExtraData = eventExtraDao.getAll().map { it.toDomain() }
            )
        )
    }

    override suspend fun importData(importSchedulePayload: ImportSchedulePayload) = db.withTransaction {
        eventExtraDao.deleteAll()
        eventDao.deleteAll()
        scheduleDao.deleteAll()
        namedScheduleDao.deleteAll()

        namedScheduleDao.insertAll(importSchedulePayload.namedSchedules.map { it.toEntity() })
        scheduleDao.insertAll(importSchedulePayload.schedules.map { it.toEntity() })
        eventDao.insertAll(importSchedulePayload.events.map { it.toEntity() })
        eventExtraDao.insertAll(importSchedulePayload.eventsExtraData.map { it.toEntity() })
    }
}