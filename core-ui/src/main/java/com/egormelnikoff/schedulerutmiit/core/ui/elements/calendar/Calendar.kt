package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarState
import java.time.LocalDate

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    calendarState: CalendarState,
    showCalendarDialog: Boolean,
    showMonth: Boolean = false,
    showYear: Boolean = true,
    onShowCalendarDialog: (Boolean) -> Unit,
    monthBadge: @Composable ((LocalDate) -> Unit) = { },
    calendarBarItem: @Composable (RowScope.(Int, LocalDate) -> Unit),
    calendarPagerItem: @Composable (Int, LocalDate) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        CalendarBar(
            calendarState = calendarState,
            showCalendarDialog = showCalendarDialog,
            showMonth = showMonth,
            showYear = showYear,
            onShowCalendarDialog = onShowCalendarDialog,
            monthBadge = monthBadge,
            calendarItem = calendarBarItem
        )

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = calendarState.getPagerDays(),
            verticalAlignment = Alignment.Top,
            pageSpacing = 12.dp
        ) { index ->
            val currentDate = remember(calendarState.calendarData.startDate, index) {
                calendarState.calendarData.startDate.plusDays(index.toLong())
            }
            calendarPagerItem(index, currentDate)
        }
    }
}