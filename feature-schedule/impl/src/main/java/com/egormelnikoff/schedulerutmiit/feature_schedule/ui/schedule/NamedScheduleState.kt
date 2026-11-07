package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules

sealed interface NamedScheduleState {
    data object Loading : NamedScheduleState
    data object Empty : NamedScheduleState
    data class Loaded(
        val namedScheduleWithSchedules: NamedScheduleWithSchedules,
        val scheduleState: ScheduleState? = null
    ) : NamedScheduleState
}