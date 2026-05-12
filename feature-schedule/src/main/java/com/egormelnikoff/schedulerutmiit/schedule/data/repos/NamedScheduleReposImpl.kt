package com.egormelnikoff.schedulerutmiit.schedule.data.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.core.database.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.core.database.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.core.database.entity.relation.toDomain
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toEntity
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import javax.inject.Inject

class NamedScheduleReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val namedScheduleDao: NamedScheduleDao,
    private val scheduleDao: ScheduleDao,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao
) : NamedScheduleRepos {
    override suspend fun save(
        namedSchedule: NamedSchedule
    ) = namedScheduleDao.insert(namedSchedule.toEntity())

    override suspend fun getCount(): Int = namedScheduleDao.getCount()

    override suspend fun getAll() = namedScheduleDao.getAll().map { it.toDomain() }

    override suspend fun getById(
        namedScheduleId: Long
    ) = namedScheduleDao.getById(namedScheduleId).toDomain()


    override suspend fun getByApiId(
        apiId: Int
    ) = namedScheduleDao.getByApiId(apiId)?.toDomain()


    override suspend fun getDefault(
    ) = namedScheduleDao.getDefault()?.toDomain()


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