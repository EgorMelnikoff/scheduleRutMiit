package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case.result

import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedScheduleWithSchedules

data class FetchNamedScheduleResult(
    val namedScheduleWithSchedules: Result<NamedScheduleWithSchedules>,
    val isSaved: Boolean
)