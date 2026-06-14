package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Calendar(
    pagerDaysState: PagerState,
    calendarItem: @Composable (Int) -> Unit
) {
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerDaysState,
        verticalAlignment = Alignment.Top,
        pageSpacing = 12.dp
    ) { index ->
        calendarItem(index)
    }
}