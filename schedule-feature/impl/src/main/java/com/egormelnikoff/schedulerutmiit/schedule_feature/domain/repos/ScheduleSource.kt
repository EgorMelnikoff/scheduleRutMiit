package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos

import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedScheduleWithSchedules


sealed interface ScheduleSource {
    data class ById(val id: Long) : ScheduleSource
    data object Default : ScheduleSource
    data class Temporary(
        val schedule: NamedScheduleWithSchedules
    ) : ScheduleSource
}