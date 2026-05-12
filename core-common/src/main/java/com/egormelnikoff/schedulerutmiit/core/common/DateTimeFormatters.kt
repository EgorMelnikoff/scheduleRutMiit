package com.egormelnikoff.schedulerutmiit.core.common

import com.egormelnikoff.schedulerutmiit.core.common.Locale.ruLocale
import java.time.format.DateTimeFormatter

object DateTimeFormatters {
    val parserFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", ruLocale)

    val dayMonthYearFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val hourMinuteFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dayMonthNameFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM")
    val yearDateMonthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
}