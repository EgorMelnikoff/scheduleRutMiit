package com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarState
import java.time.LocalDate
import java.time.format.TextStyle

@Composable
fun CalendarBar(
    calendarState: CalendarState,
    showCalendarDialog: Boolean,
    showMonth: Boolean = false,
    showYear: Boolean = true,

    onShowCalendarDialog: (Boolean) -> Unit,
    monthBadge: (@Composable (LocalDate) -> Unit) = { },
    calendarItem: @Composable RowScope.(Int, LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showMonth) {
            val firstDayOfCurrentWeek = remember(
                calendarState.currentWeekPage
            ) {
                calendarState.calendarData.startDate
                    .plusWeeks(calendarState.currentWeekPage.toLong())
                    .getFirstDayOfWeek()
            }

            val displayDate =
                if (firstDayOfCurrentWeek == calendarState.selectedDate.getFirstDayOfWeek()) {
                    calendarState.selectedDate
                } else firstDayOfCurrentWeek.plusDays(3L)

            val enabledLeftButton by remember {
                derivedStateOf {
                    calendarState.currentWeekPage != 0
                }
            }
            val enabledRightButton by remember {
                derivedStateOf {
                    calendarState.currentWeekPage != calendarState.calendarData.weeksCount - 1
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
                                calendarState.scrollWeekBackward()
                            },
                            onLongClick = {
                                calendarState.selectDate(calendarState.calendarData.startDate)
                            }
                        )
                        .padding(8.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.left),
                    tint = if (enabledLeftButton)
                        MaterialTheme.colorScheme.onBackground
                    else MaterialTheme.colorScheme.outline,
                    contentDescription = null
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .combinedClickable(
                            onClick = {
                                calendarState.selectDate(calendarState.calendarData.initialDate)
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
                        text = buildString {
                            append(
                                displayDate.month.getDisplayName(
                                    TextStyle.FULL_STANDALONE,
                                    LocalLocale.current.platformLocale
                                ).replaceFirstChar { it.uppercase() }
                            )
                            if (displayDate.year != calendarState.calendarData.initialDate.year && showYear) {
                                append(" ${displayDate.year}")
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    monthBadge.invoke(firstDayOfCurrentWeek)
                }
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .combinedClickable(
                            enabled = enabledRightButton,
                            onClick = {
                                calendarState.scrollWeekForward()
                            },
                            onLongClick = {
                                calendarState.selectDate(calendarState.calendarData.endDate)
                            }
                        )
                        .padding(8.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.right),
                    tint = if (enabledRightButton)
                        MaterialTheme.colorScheme.onBackground
                    else MaterialTheme.colorScheme.outline,
                    contentDescription = null
                )
            }
        }
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = calendarState.getPagerWeeks(),
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
                val firstDayOfWeek = calendarState.calendarData.startDate
                    .plusWeeks(index.toLong())
                    .getFirstDayOfWeek()

                for (i in 0..6) {
                    calendarItem(i, firstDayOfWeek.plusDays(i.toLong()))
                }
            }
        }
    }
}

@Composable
fun CalendarBarItem(
    currentDate: LocalDate,

    isSelected: Boolean,
    isToday: Boolean,
    isDisabled: Boolean,
    selectDate: (LocalDate) -> Unit,
    badge: @Composable () -> Unit = {}
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.secondaryContainer
        else -> Color.Unspecified
    }
    val textColor = when {
        isDisabled -> MaterialTheme.colorScheme.outline
        isSelected -> MaterialTheme.colorScheme.onPrimary
        (currentDate.dayOfWeek.value == 7) -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Column(
        modifier = Modifier.defaultMinSize(
            minWidth = 40.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    color = backgroundColor
                )
                .let {
                    if (!isDisabled) {
                        it.clickable {
                            selectDate(currentDate)
                        }
                    } else it
                }
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = currentDate.dayOfWeek.getDisplayName(
                    TextStyle.SHORT_STANDALONE,
                    LocalLocale.current.platformLocale
                ).lowercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            Text(
                text = currentDate.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
        badge.invoke()
    }
}
