package com.egormelnikoff.schedulerutmiit.domain.repos

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.NamedSchedule

interface NamedScheduleRepos {
    suspend fun saveEntity(namedScheduleEntity: NamedScheduleEntity): Long
    suspend fun deleteById(
        namedScheduleId: Long
    )

    suspend fun getCount(): Int
    suspend fun getAllEntities(): List<NamedScheduleEntity>
    suspend fun getDefault(): NamedScheduleEntity?
    suspend fun getByApiId(apiId: Int): NamedSchedule?
    suspend fun getById(namedScheduleId: Long): NamedSchedule

    suspend fun setDefaultNamedSchedule(namedScheduleId: Long)
    suspend fun updateName(
        namedScheduleId: Long,
        newName: String
    )

    suspend fun updateLastTimeUpdate(namedScheduleId: Long)
}