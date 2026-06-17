package com.egormelnikoff.schedulerutmiit.core.common.domain

data class NamedScheduleWithSchedules(
    val namedSchedule: NamedSchedule,
    val schedulesWithEvents: List<ScheduleWithEvents>
)