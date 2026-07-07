package com.egormelnikoff.schedulerutmiit.schedule.data.manager

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules

sealed interface ScheduleSource {
    data class ById(val id: Long) : ScheduleSource
    data object Default : ScheduleSource
    data class Temporary(
        val schedule: NamedScheduleWithSchedules
    ) : ScheduleSource
}