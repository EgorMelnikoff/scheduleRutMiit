package com.egormelnikoff.schedulerutmiit.domain.schedule.result

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.relation.NamedSchedule

data class ScheduleUseCaseResult(
    val savedNamedScheduleEntities: List<NamedScheduleEntity>?,
    val namedSchedule: NamedSchedule?
)