package com.egormelnikoff.schedulerutmiit.schedule.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.entity.NamedScheduleEntity

data class CurrentState(
    val namedScheduleEntities: List<NamedScheduleEntity> = emptyList(),
    val isError: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)