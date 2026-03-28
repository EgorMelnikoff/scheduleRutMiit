package com.egormelnikoff.schedulerutmiit.repos.named_schedule

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted

interface NamedScheduleRepos {
    suspend fun saveEntity(namedSchedule: NamedScheduleEntity): Long
    suspend fun deleteById(
        namedScheduleId: Long
    )

    suspend fun getCount(): Int
    suspend fun getAllEntities(): List<NamedScheduleEntity>
    suspend fun getDefault(): NamedScheduleEntity?
    suspend fun getByApiId(apiId: Int): NamedScheduleFormatted?
    suspend fun getById(namedScheduleId: Long): NamedScheduleFormatted

    suspend fun setDefaultNamedSchedule(namedScheduleId: Long)
    suspend fun updateName(
        namedScheduleId: Long,
        newName: String
    )

    suspend fun updateLastTimeUpdate(namedScheduleId: Long)
}