package com.egormelnikoff.schedulerutmiit.core.common

import java.time.format.DateTimeFormatter

object DateTimeFormatters {
    val dayMonthYearFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val hourMinuteFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dayMonthNameFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM")
    val yearDateMonthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
}