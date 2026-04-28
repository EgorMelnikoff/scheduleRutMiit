package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.result

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules

data class ScheduleUseCaseResult(
    val savedNamedSchedules: List<NamedSchedule>,
    val namedScheduleWithSchedules: NamedScheduleWithSchedules?
)