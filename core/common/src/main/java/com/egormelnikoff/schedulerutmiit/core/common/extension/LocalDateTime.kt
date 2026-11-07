package com.egormelnikoff.schedulerutmiit.core.common.extension

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.toLocalTimeWithTimeZone(): LocalTime = this
    .atZone(ZoneOffset.UTC)
    .withZoneSameInstant(ZoneId.systemDefault())
    .toLocalTime()


fun LocalDateTime.replaceDate(
    date: LocalDate
): LocalDateTime = date.atTime(this.toLocalTime())
