package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state

import androidx.compose.foundation.pager.PagerState
import java.time.LocalDate

data class CalendarState(
    val pagerWeeksState: PagerState,
    val pagerDaysState: PagerState,
    val selectedDate: LocalDate,
    val onSelectDate: (LocalDate) -> Unit
)