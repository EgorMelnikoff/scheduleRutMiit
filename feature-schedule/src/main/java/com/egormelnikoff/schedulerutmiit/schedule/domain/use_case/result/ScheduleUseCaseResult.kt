package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.result

import com.egormelnikoff.schedulerutmiit.core.common.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.common.entity.relation.NamedSchedule

data class ScheduleUseCaseResult(
    val savedNamedScheduleEntities: List<NamedScheduleEntity>,
    val namedSchedule: NamedSchedule?
)