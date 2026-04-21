package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state

import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto.ReviewUiDto
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto.ScheduleUiDto

data class ScheduleState(
    val reviewUiDto: ReviewUiDto? = null,
    val scheduleUiDto: ScheduleUiDto? = null
)