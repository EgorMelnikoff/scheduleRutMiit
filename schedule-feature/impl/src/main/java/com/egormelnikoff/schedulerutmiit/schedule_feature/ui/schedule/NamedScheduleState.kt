package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.schedule

import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedScheduleWithSchedules

sealed interface NamedScheduleState {
    data object Loading : NamedScheduleState
    data object Empty : NamedScheduleState
    data class Loaded(
        val namedScheduleWithSchedules: NamedScheduleWithSchedules,
        val scheduleState: ScheduleState? = null
    ) : NamedScheduleState
}