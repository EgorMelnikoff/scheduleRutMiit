package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos

import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedScheduleWithSchedules
import kotlinx.coroutines.flow.Flow

interface NamedScheduleRepos {
    val currentNamedSchedule: Flow<NamedScheduleWithSchedules?>
    val isSaved: Boolean
    val isLoading: Boolean

    suspend fun openTemp(namedScheduleWithSchedules: NamedScheduleWithSchedules)
    suspend fun saveOpened()
    suspend fun openDefault()

    suspend fun get(
        namedScheduleId: Long = 0,
        name: String = "",
        apiId: Int = 0,
        type: NamedScheduleType = NamedScheduleType.GROUP,
    ): NamedScheduleWithSchedules

    suspend fun getAndOpen(
        namedScheduleId: Long = 0,
        name: String = "",
        apiId: Int = 0,
        type: NamedScheduleType = NamedScheduleType.GROUP,
    )

    suspend fun save(namedScheduleWithSchedules: NamedScheduleWithSchedules): Pair<Long, Boolean>
    suspend fun delete(namedScheduleId: Long)
    suspend fun getAll(): List<NamedSchedule>
    suspend fun setDefault(namedScheduleId: Long)
}