package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model

data class NamedScheduleWithSchedules(
    val namedSchedule: NamedSchedule,
    val schedulesWithEvents: List<ScheduleWithEvents>
)