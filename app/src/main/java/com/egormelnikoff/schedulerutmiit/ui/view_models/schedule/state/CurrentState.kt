package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.NamedScheduleEntity

data class CurrentState(
    val namedScheduleEntities: List<NamedScheduleEntity> = emptyList(),
    val isError: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)