package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.bottom_sheet

import androidx.compose.material3.SelectableDates
import java.time.LocalDate
import java.time.ZoneOffset

class DateRangeSelectableDates(
    private val startMillis: Long,
    private val endMillis: Long
) : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis in startMillis..endMillis
    }

    override fun isSelectableYear(year: Int): Boolean {
        val yearStart =
            LocalDate.of(year, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val yearEnd =
            LocalDate.of(year, 12, 31).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

        return startMillis <= yearEnd && endMillis >= yearStart
    }
}