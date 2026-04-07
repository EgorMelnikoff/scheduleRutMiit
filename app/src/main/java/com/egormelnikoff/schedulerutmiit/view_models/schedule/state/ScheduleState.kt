package com.egormelnikoff.schedulerutmiit.view_models.schedule.state

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ui_dto.NamedScheduleUiDto

@Keep
data class ScheduleState(
    val savedNamedScheduleEntities: List<NamedScheduleEntity> = emptyList(),
    val defaultNamedSchedule: NamedScheduleUiDto? = null,
    val currentNamedSchedule: NamedScheduleUiDto? = null,
    val isRefreshing: Boolean = false,
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)