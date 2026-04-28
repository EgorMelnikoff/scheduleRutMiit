package com.egormelnikoff.schedulerutmiit.schedule.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents

interface ScheduleRepos {
    suspend fun save(
        namedScheduleId: Long,
        scheduleWithEvents: ScheduleWithEvents
    ): Long

    suspend fun saveAll(
        namedScheduleId: Long,
        scheduleWithEvents: List<ScheduleWithEvents>
    )

    suspend fun deleteById(scheduleId: Long)

    suspend fun updateEvents(
        scheduleId: Long,
        scheduleWithEvents: ScheduleWithEvents
    )

    suspend fun setDefault(
        namedScheduleId: Long,
        scheduleId: Long
    )
}