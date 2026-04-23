package com.egormelnikoff.schedulerutmiit.core.common.extension

import com.egormelnikoff.schedulerutmiit.core.common.dto.RecurrenceDto
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

fun LocalDate.getFirstDayOfWeek(): LocalDate = this.minusDays(this.dayOfWeek.value - 1L)

fun LocalDate.getCurrentWeek(
    startDate: LocalDate,
    recurrence: RecurrenceDto?
): Int {
    recurrence?.let {
        val weeksFromStart = abs(ChronoUnit.WEEKS.between(this, startDate)).plus(1).toInt()
        return ((weeksFromStart + recurrence.firstWeekNumber) % recurrence.interval).plus(1)
    }
    return -1
}
