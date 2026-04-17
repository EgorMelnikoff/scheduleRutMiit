package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.NamedSchedule

data class ScheduleUseCaseResult(
    val savedNamedScheduleEntities: List<NamedScheduleEntity>?,
    val namedSchedule: NamedSchedule?
)