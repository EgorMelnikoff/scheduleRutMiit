package com.egormelnikoff.schedulerutmiit.schedule_feature.data.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.ScheduleDatabase
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.ImportSchedulePayload
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.v2.ExportDataV2
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.mapper.toEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.mapper.toImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos.ExportRepos
import javax.inject.Inject

class ExportReposImpl @Inject constructor(
    private val db: ScheduleDatabase,
    private val namedScheduleDao: NamedScheduleDao,
    private val scheduleDao: ScheduleDao,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao
) : ExportRepos {
    override suspend fun getExportData(): ExportDataV2 = db.withTransaction {
        return@withTransaction ExportDataV2(
            importSchedulePayload = ImportSchedulePayload(
                namedSchedules = namedScheduleDao.getAll().map { it.toImport() },
                schedules = scheduleDao.getAll().map { it.toImport() },
                events = eventDao.getAll().map { it.toImport() },
                eventsExtraData = eventExtraDao.getAll().map { it.toImport() }
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