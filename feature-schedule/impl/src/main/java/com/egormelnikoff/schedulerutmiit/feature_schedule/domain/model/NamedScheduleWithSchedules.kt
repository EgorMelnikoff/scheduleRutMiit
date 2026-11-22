package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model

internal data class NamedScheduleWithSchedules(
    val namedSchedule: NamedSchedule,
    val schedulesWithEvents: List<ScheduleWithEvents>
)