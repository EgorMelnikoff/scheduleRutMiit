package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result

data class FetchNamedScheduleResult(
    val namedSchedule: Result<NamedSchedule>,
    val isSaved: Boolean
)