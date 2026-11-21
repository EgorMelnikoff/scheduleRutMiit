package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules


sealed interface ScheduleSource {
    data class ById(val id: Long) : ScheduleSource
    data object Default : ScheduleSource
    data class Temporary(
        val schedule: NamedScheduleWithSchedules
    ) : ScheduleSource
}