package com.egormelnikoff.schedulerutmiit.schedule.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import kotlinx.coroutines.flow.Flow

interface NamedScheduleRepos {
    suspend fun save(namedSchedule: NamedSchedule): Long
    suspend fun deleteById(
        namedScheduleId: Long
    )

    suspend fun getCount(): Int
    fun observeAll(): Flow<List<NamedSchedule>>
    suspend fun getAll(): List<NamedSchedule>
    suspend fun getDefault(): NamedSchedule?
    suspend fun getByApiId(apiId: Int): NamedScheduleWithSchedules?
    suspend fun getById(namedScheduleId: Long): NamedScheduleWithSchedules

    suspend fun setDefaultNamedSchedule(namedScheduleId: Long)
    suspend fun updateName(
        namedScheduleId: Long,
        newName: String
    )

    suspend fun updateLastTimeUpdate(namedScheduleId: Long)
}