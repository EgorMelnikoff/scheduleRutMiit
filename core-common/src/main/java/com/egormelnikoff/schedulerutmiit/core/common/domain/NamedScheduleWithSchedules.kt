package com.egormelnikoff.schedulerutmiit.core.common.domain

import androidx.annotation.Keep

@Keep
data class NamedScheduleWithSchedules(
    val namedSchedule: NamedSchedule,
    val scheduleWithEvents: List<ScheduleWithEvents>
)