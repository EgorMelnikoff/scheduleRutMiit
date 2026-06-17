package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state

import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class CalendarData(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val initialDate: LocalDate,

    val daysCount: Int,
    val weeksCount: Int,

    val weeksPagerInitialIndex: Int,
    val daysPagerInitialIndex: Int,
) {
    companion object {
        operator fun invoke(
            startDate: LocalDate? = null,
            endDate: LocalDate? = null
        ): CalendarData {
            val today = LocalDate.now()

            val newStartDate = startDate ?: today.minusYears(5)
            val newEndDate = endDate ?: today.plusYears(5)

            val weeksCount = ChronoUnit.WEEKS.between(
                newStartDate.getFirstDayOfWeek(),
                newEndDate.getFirstDayOfWeek()
            ).plus(1).toInt()

            val daysCount = ChronoUnit.DAYS.between(
                newStartDate,
                newEndDate
            ).plus(1).toInt()

            val defaultDate = when {
                today < newStartDate -> newStartDate
                today > newEndDate -> newEndDate
                else -> today
            }

            val weeksPagerDefaultIndex = ChronoUnit.WEEKS.between(
                newStartDate.getFirstDayOfWeek(),
                defaultDate.getFirstDayOfWeek()
            ).toInt()

            val daysPagerDefaultIndex = ChronoUnit.DAYS.between(
                newStartDate,
                defaultDate
            ).toInt()

            return CalendarData(
                startDate = newStartDate,
                endDate = newEndDate,
                initialDate = defaultDate,
                weeksCount = weeksCount,
                weeksPagerInitialIndex = weeksPagerDefaultIndex,
                daysCount = daysCount,
                daysPagerInitialIndex = daysPagerDefaultIndex
            )
        }
    }
}