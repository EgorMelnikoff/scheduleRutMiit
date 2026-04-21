package com.egormelnikoff.schedulerutmiit.data.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.data.local.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.data.local.db.dao.EventDao
import com.egormelnikoff.schedulerutmiit.data.local.db.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.data.local.db.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.data.local.db.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import javax.inject.Inject

class NamedScheduleReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val namedScheduleDao: NamedScheduleDao,
    private val scheduleDao: ScheduleDao,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao
) : NamedScheduleRepos {
    override suspend fun saveEntity(
        namedScheduleEntity: NamedScheduleEntity
    ) = namedScheduleDao.insert(namedScheduleEntity)

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