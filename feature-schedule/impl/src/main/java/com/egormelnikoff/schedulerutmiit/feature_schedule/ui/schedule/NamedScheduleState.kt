package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.ScheduleVo

sealed interface NamedScheduleState {
    data object Loading : NamedScheduleState
    data object Empty : NamedScheduleState
    data class Loaded(
        val namedScheduleWithSchedules: NamedScheduleWithSchedules,
        val scheduleVo: ScheduleVo? = null
    ) : NamedScheduleState
}