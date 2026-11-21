package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.result

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules

data class FetchNamedScheduleResult(
    val namedScheduleWithSchedules: Result<NamedScheduleWithSchedules>,
    val isSaved: Boolean
)