package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos

import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import kotlinx.coroutines.flow.Flow

internal interface NamedScheduleRepos {
    val currentNamedSchedule: Flow<NamedScheduleWithSchedules?>
    val isSaved: Boolean

    suspend fun openTemp(namedScheduleWithSchedules: NamedScheduleWithSchedules)
    suspend fun saveOpened()
    suspend fun openDefault()

    suspend fun get(
        namedScheduleId: Long = 0,
        name: String = "",
        apiId: Int = 0,
        type: NamedScheduleType = NamedScheduleType.GROUP,
    ): Pair<Boolean,NamedScheduleWithSchedules>

    suspend fun getById(
        namedScheduleId: Long
    ): NamedScheduleWithSchedules

    suspend fun getDefault(): NamedSchedule?

    fun observeDefault(): Flow<NamedScheduleWithSchedules?>

    suspend fun getAndOpen(
        namedScheduleId: Long = 0,
        name: String = "",
        apiId: Int = 0,
        type: NamedScheduleType = NamedScheduleType.GROUP,
    )

    suspend fun save(namedScheduleWithSchedules: NamedScheduleWithSchedules): Pair<Long, Boolean>
    suspend fun delete(namedScheduleId: Long)
    fun observeAll(): Flow<List<NamedSchedule>>
    suspend fun getAll(): List<NamedSchedule>
    suspend fun setDefault(namedScheduleId: Long)
    suspend fun updateName(
        namedScheduleId: Long,
        newName: String
    )

    suspend fun updateLastTimeUpdate(namedScheduleId: Long)
}