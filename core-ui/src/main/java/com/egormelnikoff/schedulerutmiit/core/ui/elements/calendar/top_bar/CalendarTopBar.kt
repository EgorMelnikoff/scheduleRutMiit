package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.top_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarData
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle

@Composable
fun CalendarTopBar(
    calendarData: CalendarData,
    calendarState: CalendarState,
    showCalendarDialog: Boolean,
    showMonth: Boolean = false,

    onShowCalendarDialog: (Boolean) -> Unit,
    monthBadge: (@Composable (LocalDate) -> Unit)? = null,
    calendarItem: @Composable (Int, LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showMonth) {
            val scope = rememberCoroutineScope()

            val firstDayOfCurrentWeek = remember(
                calendarState.pagerWeeksState.currentPage
            ) {
                calendarData.startDate
                    .plusWeeks(calendarState.pagerWeeksState.currentPage.toLong())
                    .getFirstDayOfWeek()
            }

            val displayDate =
                if (firstDayOfCurrentWeek == calendarState.selectedDate.getFirstDayOfWeek()) {
                    calendarState.selectedDate
                } else firstDayOfCurrentWeek.plusDays(3L)

            val enabledLeftButton by remember {
                derivedStateOf {
                    calendarState.pagerWeeksState.currentPage != 0
                }
            }
            val enabledRightButton by remember {
                derivedStateOf {
                    calendarState.pagerWeeksState.currentPage != calendarData.weeksCount - 1
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .combinedClickable(
                            enabled = enabledLeftButton,
                            onClick = {
                                scope.launch {
                                    calendarState.pagerWeeksState.animateScrollToPage(
                                        calendarState.pagerWeeksState.currentPage - 1
                                    )
                                }
                            },
                            onLongClick = {
                                scope.launch {
                                    calendarState.pagerWeeksState.animateScrollToPage(0)
                                    calendarState.pagerDaysState.scrollToPage(0)
                                }
                            }
                        )
                        .padding(8.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.left),
                    tint = if (enabledLeftButton)
                        MaterialTheme.colorScheme.onBackground
                    else MaterialTheme.colorScheme.secondaryContainer,
                    contentDescription = null
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .combinedClickable(
                            onClick = {
                                scope.launch {
                                    calendarState.pagerWeeksState.animateScrollToPage(
                                        calendarData.weeksPagerDefaultIndex
                                    )
                                }
                                calendarState.onSelectDate(calendarData.defaultDate)
                            },
                            onLongClick = {
                                onShowCalendarDialog(!showCalendarDialog)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .height(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = displayDate.month.getDisplayName(
                            TextStyle.FULL_STANDALONE,
                            LocalLocale.current.platformLocale
                        ).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    monthBadge?.invoke(firstDayOfCurrentWeek)
                }
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .combinedClickable(
                            enabled = enabledRightButton,
                            onClick = {
                                scope.launch {
                                    calendarState.pagerWeeksState.animateScrollToPage(
                                        calendarState.pagerWeeksState.currentPage + 1
                                    )
                                }
                            },
                            onLongClick = {
                                scope.launch {
                                    calendarState.pagerWeeksState.animateScrollToPage(
                                        calendarData.weeksCount - 1
                                    )
                                    calendarState.pagerDaysState.scrollToPage(
                                        calendarData.daysCount - 1
                                    )
                                }
                            }
                        )
                        .padding(8.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.right),
                    tint = if (enabledRightButton)
                        MaterialTheme.colorScheme.onBackground
                    else MaterialTheme.colorScheme.secondaryContainer,
                    contentDescription = null
                )
            }
        }
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = calendarState.pagerWeeksState,
            verticalAlignment = Alignment.Top,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(
                top = 8.dp, bottom = 12.dp,
                start = 16.dp, end = 16.dp
            )
        ) { index ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val firstDayOfWeek = calendarData.startDate
                    .plusWeeks(index.toLong())
                    .getFirstDayOfWeek()

                for (i in 0..6) {
                    calendarItem(i, firstDayOfWeek.plusDays(i.toLong()))
                }
            }
        }
    }
}