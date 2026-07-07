package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state

import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class CalendarData(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val initialDate: LocalDate,

    val daysCount: Int,
    val weeksCount: Int
) {
    fun getInitialDayIndex(today: LocalDate = LocalDate.now()) = ChronoUnit.DAYS.between(
        startDate,
        today
    ).toInt()

    fun getInitialWeekIndex(today: LocalDate = LocalDate.now()) = ChronoUnit.WEEKS.between(
        startDate.getFirstDayOfWeek(),
        today.getFirstDayOfWeek()
    ).toInt()

    companion object {
        operator fun invoke(
            startDate: LocalDate,
            endDate: LocalDate
        ): CalendarData {
            val weeksCount = ChronoUnit.WEEKS.between(
                startDate.getFirstDayOfWeek(),
                endDate.getFirstDayOfWeek()
            ).plus(1).toInt()

            val daysCount = ChronoUnit.DAYS.between(
                startDate,
                endDate
            ).plus(1).toInt()

            val today = LocalDate.now()

            val initialDate = when {
                today < startDate -> startDate
                today > endDate -> endDate
                else -> today
            }

            return CalendarData(
                startDate = startDate,
                endDate = endDate,
                initialDate = initialDate,
                weeksCount = weeksCount,
                daysCount = daysCount
            )
        }
    }
}