package com.egormelnikoff.schedulerutmiit.domain.schedule.result

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.Result

data class FetchNamedScheduleResult(
    val namedScheduleFormatted: Result<NamedScheduleFormatted>,
    val isSaved: Boolean
)