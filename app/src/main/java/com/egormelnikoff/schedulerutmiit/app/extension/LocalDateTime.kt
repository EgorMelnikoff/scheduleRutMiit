package com.egormelnikoff.schedulerutmiit.app.extension

import com.egormelnikoff.schedulerutmiit.app.enums.DayPeriod
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.toLocalTimeWithTimeZone(): LocalTime = this
    .atZone(ZoneOffset.UTC)
    .withZoneSameInstant(ZoneId.systemDefault())
    .toLocalTime()

fun LocalDateTime.dayPeriod(): DayPeriod {
    return when (toLocalTime()) {
        in LocalTime.of(6, 0)..LocalTime.of(11, 59) -> DayPeriod.MORNING
        in LocalTime.of(12, 0)..LocalTime.of(17, 59) -> DayPeriod.DAY
        in LocalTime.of(18, 0)..LocalTime.of(21, 59) -> DayPeriod.EVENING
        else -> DayPeriod.NIGHT
    }
}

fun LocalDateTime.replaceDate(
    date: LocalDate
): LocalDateTime = date.atTime(this.toLocalTime())
