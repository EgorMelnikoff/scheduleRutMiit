package com.egormelnikoff.schedulerutmiit.domain.schedule.result

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted

data class OpenSavedScheduleResult(
    val savedNamedSchedules: List<NamedScheduleEntity>?,
    val namedScheduleFormatted: NamedScheduleFormatted?
)