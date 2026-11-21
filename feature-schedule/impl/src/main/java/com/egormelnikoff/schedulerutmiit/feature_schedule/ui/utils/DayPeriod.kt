package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils

import java.time.LocalDateTime
import java.time.LocalTime

enum class DayPeriod {
    NIGHT, MORNING, DAY, EVENING
}

fun LocalDateTime.dayPeriod(): DayPeriod {
    return when (toLocalTime()) {
        in LocalTime.of(6, 0)..LocalTime.of(11, 59) -> DayPeriod.MORNING
        in LocalTime.of(12, 0)..LocalTime.of(17, 59) -> DayPeriod.DAY
        in LocalTime.of(18, 0)..LocalTime.of(21, 59) -> DayPeriod.EVENING
        else -> DayPeriod.NIGHT
    }
}
