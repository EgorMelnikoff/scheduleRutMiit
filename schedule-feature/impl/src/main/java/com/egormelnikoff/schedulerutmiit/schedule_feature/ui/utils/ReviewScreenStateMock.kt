package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.utils

import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.ReviewState
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.SummaryState
import java.time.LocalDate

val date: LocalDate =  LocalDate.of(2026, 9, 15)

val reviewStateMock = ReviewState(
    currentDate = LocalDate.of(2026, 9, 15),
    dayPeriod = DayPeriod.NIGHT,
    summaryState = SummaryState(
        date
    ),
    namedSchedules = listOf(
        namedScheduleMock,
        namedScheduleMock.copy(
            shortName = "УВВ-411",
            fullName = "УВВ-411"
        )
    )
)