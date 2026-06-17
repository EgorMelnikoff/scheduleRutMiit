package com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ReviewState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ScheduleState

data class NamedScheduleState(
    val namedScheduleWithSchedules: NamedScheduleWithSchedules? = null,
    val scheduleState: ScheduleState? = null,
    val reviewState: ReviewState? = null
)