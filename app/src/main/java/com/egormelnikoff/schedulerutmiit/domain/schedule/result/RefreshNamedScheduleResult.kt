package com.egormelnikoff.schedulerutmiit.domain.schedule.result

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted

data class RefreshNamedScheduleResult(
    val savedNamedSchedules: List<NamedScheduleEntity>?,
    val namedScheduleFormatted: NamedScheduleFormatted?,
    val isSaved: Boolean
)