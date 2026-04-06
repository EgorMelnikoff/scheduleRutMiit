package com.egormelnikoff.schedulerutmiit.repos.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.relation.Schedule

interface ScheduleRepos {
    suspend fun save(
        namedScheduleId: Long,
        schedule: Schedule
    ): Long

    suspend fun saveAllSchedules(
        namedScheduleId: Long,
        schedules: List<Schedule>
    )

    suspend fun deleteById(scheduleId: Long)

    suspend fun updateEvents(
        scheduleId: Long,
        schedule: Schedule
    )

    suspend fun setDefaultSchedule(
        namedScheduleId: Long,
        scheduleId: Long
    )
}