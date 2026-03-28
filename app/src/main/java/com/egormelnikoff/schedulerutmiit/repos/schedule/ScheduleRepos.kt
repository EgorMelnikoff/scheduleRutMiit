package com.egormelnikoff.schedulerutmiit.repos.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted

interface ScheduleRepos {
    suspend fun save(
        namedScheduleId: Long,
        scheduleFormatted: ScheduleFormatted
    ): Long

    suspend fun saveAllSchedules(
        namedScheduleId: Long,
        schedules: List<ScheduleFormatted>
    )

    suspend fun deleteById(scheduleId: Long)

    suspend fun updateEvents(
        scheduleId: Long,
        scheduleFormatted: ScheduleFormatted
    )

    suspend fun setDefaultSchedule(
        namedScheduleId: Long,
        scheduleId: Long
    )
}