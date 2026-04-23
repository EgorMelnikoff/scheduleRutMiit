package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.database.entity.relation.NamedSchedule

data class FetchNamedScheduleResult(
    val namedSchedule: Result<NamedSchedule>,
    val isSaved: Boolean
)