package com.egormelnikoff.schedulerutmiit.view_models.schedule

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity

@Keep
data class ScheduleState(
    val savedNamedSchedules: List<NamedScheduleEntity> = emptyList(),
    val defaultNamedScheduleData: NamedScheduleData? = null,
    val currentNamedScheduleData: NamedScheduleData? = null,
    val isRefreshing: Boolean = false,
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)