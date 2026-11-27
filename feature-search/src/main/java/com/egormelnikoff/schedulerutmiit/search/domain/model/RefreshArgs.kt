package com.egormelnikoff.schedulerutmiit.search.domain.model

import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.ScheduleType

internal data class RefreshArgs(
    val name: String,
    val apiId: Int,
    val type: ScheduleType
)