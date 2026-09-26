package com.egormelnikoff.schedulerutmiit.schedule.ui.utils

import com.egormelnikoff.schedulerutmiit.core.common.domain.Recurrence
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import java.time.LocalDate

val periodicScheduleMock = Schedule(
    namedScheduleId = 0,
    timetableId = "",
    timetableType = TimetableType.PERIODIC,
    downloadUrl = null,
    startDate = LocalDate.of(2026, 9, 1),
    endDate = LocalDate.of(2027, 1, 4),
    recurrence = Recurrence(
        interval = 2,
        firstWeekNumber = 1
    )
)