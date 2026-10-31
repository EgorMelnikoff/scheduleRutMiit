package com.egormelnikoff.schedulerutmiit.schedule_feature.api.repos

import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleWithSchedules

sealed interface ScheduleSource {
    data class ById(val id: Long) : ScheduleSource
    data object Default : ScheduleSource
    data class Temporary(
        val schedule: NamedScheduleWithSchedules
    ) : ScheduleSource
}