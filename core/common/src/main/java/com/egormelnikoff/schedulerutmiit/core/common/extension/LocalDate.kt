package com.egormelnikoff.schedulerutmiit.core.common.extension

import java.time.LocalDate
import java.time.ZoneId

fun LocalDate.getFirstDayOfWeek(): LocalDate = this.minusDays(this.dayOfWeek.value - 1L)


fun LocalDate.getStartMs(
    zone: ZoneId? = null
): Long {
    val zoneId = zone ?: ZoneId.systemDefault()

    return this.atStartOfDay(zoneId).toInstant().toEpochMilli()
}