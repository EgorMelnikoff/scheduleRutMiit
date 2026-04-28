package com.egormelnikoff.schedulerutmiit.core.common.extension

import java.time.LocalDate

fun LocalDate.getFirstDayOfWeek(): LocalDate = this.minusDays(this.dayOfWeek.value - 1L)