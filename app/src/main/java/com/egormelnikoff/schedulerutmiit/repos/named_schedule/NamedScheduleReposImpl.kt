package com.egormelnikoff.schedulerutmiit.repos.named_schedule

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.datasource.local.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.ScheduleDao
import javax.inject.Inject

class NamedScheduleReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val namedScheduleDao: NamedScheduleDao,
    private val scheduleDao: ScheduleDao,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao
): NamedScheduleRepos {
    override suspend fun saveEntity(
        namedSchedule: NamedScheduleEntity
    ) = namedScheduleDao.insert(namedSchedule)

    override suspend fun getCount(): Int = namedScheduleDao.getCount()

    override suspend fun getAllEntities() = namedScheduleDao.getAllEntities()

    override suspend fun getById(
        namedScheduleId: Long
    ) = namedScheduleDao.getById(namedScheduleId)


    override suspend fun getByApiId(
        apiId: Int
    ) = namedScheduleDao.getByApiId(apiId)


    override suspend fun getDefault(
    ) = namedScheduleDao.getDefault()


    override suspend fun setDefaultNamedSchedule(
        namedScheduleId: Long
    ) = db.withTransaction {
        namedScheduleDao.setDefault(namedScheduleId)
        namedScheduleDao.setNonDefault(namedScheduleId)
    }


    override suspend fun updateName(
        namedScheduleId: Long,
        newName: String
    ) = namedScheduleDao.updateName(
        namedScheduleId = namedScheduleId,
        fullName = newName,
        shortName = newName
    )

    override suspend fun updateLastTimeUpdate(namedScheduleId: Long) =
        namedScheduleDao.updateLastTimeUpdate(namedScheduleId)

    override suspend fun deleteById(
        namedScheduleId: Long
    ) = db.withTransaction {
        namedScheduleDao.deleteById(namedScheduleId)
        val schedulesId = scheduleDao.getIds(namedScheduleId)
        scheduleDao.deleteByNamedScheduleId(namedScheduleId)
        schedulesId.forEach { scheduleId ->
            eventDao.deleteByScheduleId(scheduleId)
            eventExtraDao.deleteByScheduleId(scheduleId)
        }
    }
}