package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model

data class NamedScheduleWithSchedules(
    val namedSchedule: NamedSchedule,
    val schedulesWithEvents: List<ScheduleWithEvents>
)