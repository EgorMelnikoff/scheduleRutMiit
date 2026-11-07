package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.dao.EventDao
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.dao.EventExtraDao
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.dao.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.dao.ScheduleDao
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NamedScheduleDataSource @Inject constructor(
    private val db: ScheduleDatabase,
    private val namedScheduleDao: NamedScheduleDao,
    private val scheduleDao: ScheduleDao,
    private val eventDao: EventDao,
    private val eventExtraDao: EventExtraDao
) {
    suspend fun save(
        namedSchedule: NamedSchedule
    ) = namedScheduleDao.insert(namedSchedule.toEntity())

    suspend fun getCount(): Int = namedScheduleDao.count()

    suspend fun getAll() = namedScheduleDao.getAll().map { it.toDomain() }

    suspend fun getById(
        namedScheduleId: Long
    ) = namedScheduleDao.getById(namedScheduleId).toDomain()

    suspend fun getByApiId(
        apiId: Int
    ) = namedScheduleDao.getByApiId(apiId)?.toDomain()

    suspend fun getDefault() = namedScheduleDao.getDefault()?.toDomain()


    fun observeAll(): Flow<List<NamedSchedule>> {
        return namedScheduleDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun observeById(namedScheduleId: Long) =
        namedScheduleDao.observeById(namedScheduleId).map { it?.toDomain() }

    fun observeDefault(): Flow<NamedScheduleWithSchedules?> =
        namedScheduleDao.observeDefault().map { it?.toDomain() }


    suspend fun setDefault(
        namedScheduleId: Long
    ) = db.withTransaction {
        namedScheduleDao.setDefault(namedScheduleId)
        namedScheduleDao.setNonDefault(namedScheduleId)
    }

    suspend fun updateName(
        namedScheduleId: Long,
        newName: String
    ) = namedScheduleDao.updateName(
        namedScheduleId = namedScheduleId,
        fullName = newName,
        shortName = newName
    )

    suspend fun updateLastTimeUpdate(namedScheduleId: Long) =
        namedScheduleDao.updateLastTimeUpdate(namedScheduleId)


    suspend fun deleteById(
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