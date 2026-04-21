package com.egormelnikoff.schedulerutmiit.app.extension

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalTime.toUtcTime(date: LocalDate): LocalTime = this
    .atDate(date)
    .atZone(ZoneId.systemDefault())
    .withZoneSameInstant(ZoneOffset.UTC)
    .toLocalTime()
