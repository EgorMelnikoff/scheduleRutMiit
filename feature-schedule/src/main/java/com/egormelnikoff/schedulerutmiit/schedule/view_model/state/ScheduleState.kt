package com.egormelnikoff.schedulerutmiit.schedule.view_model.state

import com.egormelnikoff.schedulerutmiit.schedule.view_model.state.ui_dto.ReviewUiDto
import com.egormelnikoff.schedulerutmiit.schedule.view_model.state.ui_dto.ScheduleUiDto

data class ScheduleState(
    val reviewUiDto: ReviewUiDto? = null,
    val scheduleUiDto: ScheduleUiDto? = null
)