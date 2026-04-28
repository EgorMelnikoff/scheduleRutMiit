package com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule

data class CurrentState(
    val namedSchedules: List<NamedSchedule> = emptyList(),
    val isError: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)