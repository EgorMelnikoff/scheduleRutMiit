package com.egormelnikoff.schedulerutmiit.schedule.domain.manager

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import kotlinx.coroutines.flow.Flow

interface ScheduleManager {
    val currentNamedSchedule: Flow<NamedScheduleWithSchedules?>
    val isTemp: Boolean
    suspend fun openTemp(namedScheduleWithSchedules: NamedScheduleWithSchedules)
    suspend fun saveTemp()
    suspend fun openDefault()

    suspend fun openSaved(id: Long)
}