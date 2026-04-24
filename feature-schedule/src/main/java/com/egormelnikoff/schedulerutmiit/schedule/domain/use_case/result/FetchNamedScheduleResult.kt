package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.result

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.entity.relation.NamedSchedule

data class FetchNamedScheduleResult(
    val namedSchedule: Result<NamedSchedule>,
    val isSaved: Boolean
)