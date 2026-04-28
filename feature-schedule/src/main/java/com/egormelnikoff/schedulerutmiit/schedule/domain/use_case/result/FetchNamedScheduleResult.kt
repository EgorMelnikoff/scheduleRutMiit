package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.result

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.core.common.result.Result

data class FetchNamedScheduleResult(
    val namedScheduleWithSchedules: Result<NamedScheduleWithSchedules>,
    val isSaved: Boolean
)