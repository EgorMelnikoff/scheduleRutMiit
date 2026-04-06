package com.egormelnikoff.schedulerutmiit.domain.schedule.result

import com.egormelnikoff.schedulerutmiit.app.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.app.network.result.Result

data class FetchNamedScheduleResult(
    val namedSchedule: Result<NamedSchedule>,
    val isSaved: Boolean
)