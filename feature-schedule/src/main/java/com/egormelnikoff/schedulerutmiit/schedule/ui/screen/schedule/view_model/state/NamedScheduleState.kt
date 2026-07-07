package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules

sealed interface NamedScheduleState {
    data object Loading : NamedScheduleState
    data object Empty : NamedScheduleState
    data class Loaded(
        val namedScheduleWithSchedules: NamedScheduleWithSchedules,
        val scheduleState: ScheduleState? = null
    ) : NamedScheduleState
}