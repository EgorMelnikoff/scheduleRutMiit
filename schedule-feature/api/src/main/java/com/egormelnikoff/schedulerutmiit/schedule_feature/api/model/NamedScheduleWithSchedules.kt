package com.egormelnikoff.schedulerutmiit.schedule_feature.api.model

data class NamedScheduleWithSchedules(
    val namedSchedule: NamedSchedule,
    val schedulesWithEvents: List<ScheduleWithEvents>
)