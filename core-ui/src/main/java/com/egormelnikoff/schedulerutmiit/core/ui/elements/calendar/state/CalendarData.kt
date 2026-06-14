package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state

import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

data class CalendarData(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val defaultDate: LocalDate,

    val daysCount: Int,
    val weeksCount: Int,

    val weeksPagerDefaultIndex: Int,
    val daysPagerDefaultIndex: Int,
) {
    companion object {
        operator fun invoke(
            startDate: LocalDate,
            endDate: LocalDate
        ): CalendarData {
            val today = LocalDate.now()

            val weeksCount = ChronoUnit.WEEKS.between(
                startDate.getFirstDayOfWeek(),
                endDate.getFirstDayOfWeek()
            ).plus(1).toInt()

            val daysCount = ChronoUnit.DAYS.between(
                startDate,
                endDate
            ).plus(1).toInt()

            val defaultDate: LocalDate
            val weeksStartIndex: Int
            val daysStartIndex: Int

            if (today in startDate..endDate) {
                weeksStartIndex = abs(
                    ChronoUnit.WEEKS.between(
                        startDate.getFirstDayOfWeek(),
                        today.getFirstDayOfWeek()
                    ).toInt()
                )
                daysStartIndex = abs(
                    ChronoUnit.DAYS.between(
                        startDate,
                        today
                    ).toInt()
                )
                defaultDate = today
            } else if (today < startDate) {
                weeksStartIndex = 0
                daysStartIndex = 0
                defaultDate = startDate
            } else {
                weeksStartIndex = weeksCount
                daysStartIndex = weeksCount * 7
                defaultDate = endDate
            }
            return CalendarData(
                startDate = startDate,
                endDate = endDate,
                defaultDate = defaultDate,
                weeksCount = weeksCount,
                weeksPagerDefaultIndex = weeksStartIndex,
                daysCount = daysCount,
                daysPagerDefaultIndex = daysStartIndex
            )
        }
    }
}