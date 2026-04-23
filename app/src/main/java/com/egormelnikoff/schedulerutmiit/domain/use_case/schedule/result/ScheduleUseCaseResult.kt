package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result

import com.egormelnikoff.schedulerutmiit.core.database.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.relation.NamedSchedule

data class ScheduleUseCaseResult(
    val savedNamedScheduleEntities: List<NamedScheduleEntity>,
    val namedSchedule: NamedSchedule?
)