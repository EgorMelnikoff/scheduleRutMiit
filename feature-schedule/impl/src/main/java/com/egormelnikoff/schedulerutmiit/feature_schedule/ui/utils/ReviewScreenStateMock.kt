package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils

import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.SummaryState
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