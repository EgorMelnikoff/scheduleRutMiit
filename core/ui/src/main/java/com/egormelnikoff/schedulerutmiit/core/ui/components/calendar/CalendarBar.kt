package com.egormelnikoff.schedulerutmiit.core.ui.components.calendar

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
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
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
            .background(AppTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
    ) {
        if (showMonth) {
            val firstDayOfCurrentWeek = remember(
                calendarState.currentWeekPage,
                calendarState.calendarData
            ) {
                calendarState.calendarData.startDate
                    .plusWeeks(calendarState.currentWeekPage.toLong())
                    .getFirstDayOfWeek()
            }

            val displayDate = remember(firstDayOfCurrentWeek, calendarState.selectedDate) {
                if (firstDayOfCurrentWeek == calendarState.selectedDate.getFirstDayOfWeek()) {
                    calendarState.selectedDate
                } else firstDayOfCurrentWeek.plusDays(3L)
            }


            val enabledLeftButton by remember(calendarState.calendarData) {
                derivedStateOf {
                    calendarState.currentWeekPage != 0
                }
            }
            val enabledRightButton by remember(calendarState.calendarData) {
                derivedStateOf {
                    calendarState.currentWeekPage != calendarState.calendarData.weeksCount - 1
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.paddings.padding8),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
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
                        .padding(AppTheme.paddings.padding8),
                    imageVector = ImageVector.vectorResource(R.drawable.left),
                    tint = if (enabledLeftButton)
                        AppTheme.colors.onBackground
                    else AppTheme.colors.outline,
                    contentDescription = null
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .combinedClickable(
                            onClick = {
                                calendarState.selectInitialDate()
                            },
                            onLongClick = {
                                onShowCalendarDialog(!showCalendarDialog)
                            }
                        )
                        .padding(horizontal = AppTheme.paddings.padding8, vertical = AppTheme.paddings.padding4)
                        .height(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding6, Alignment.CenterHorizontally)
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
                        style = AppTheme.typography.bodyLarge,
                        color = AppTheme.colors.onBackground,
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
                        .padding(AppTheme.paddings.padding8),
                    imageVector = ImageVector.vectorResource(R.drawable.right),
                    tint = if (enabledRightButton)
                        AppTheme.colors.onBackground
                    else AppTheme.colors.outline,
                    contentDescription = null
                )
            }
        }
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = calendarState.getPagerWeeks(),
            verticalAlignment = Alignment.Top,
            pageSpacing = AppTheme.paddings.padding16,
            contentPadding = PaddingValues(
                top = AppTheme.paddings.padding8, bottom = AppTheme.paddings.padding12,
                start = AppTheme.paddings.padding16, end = AppTheme.paddings.padding16
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
        isSelected -> AppTheme.colors.primary
        isToday -> AppTheme.colors.secondaryContainer
        else -> Color.Unspecified
    }
    val textColor = when {
        isDisabled -> AppTheme.colors.outline
        isSelected -> AppTheme.colors.onPrimary
        (currentDate.dayOfWeek.value == 7) -> AppTheme.colors.error
        else -> AppTheme.colors.onSecondaryContainer
    }

    Column(
        modifier = Modifier.defaultMinSize(
            minWidth = 40.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12)
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
                .padding(vertical = AppTheme.paddings.padding10, horizontal = AppTheme.paddings.padding12),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8, Alignment.CenterVertically)
        ) {
            Text(
                text = currentDate.dayOfWeek.getDisplayName(
                    TextStyle.SHORT_STANDALONE,
                    LocalLocale.current.platformLocale
                ).lowercase(),
                style = AppTheme.typography.bodyMedium,
                color = textColor
            )
            Text(
                text = currentDate.dayOfMonth.toString(),
                style = AppTheme.typography.bodyMedium,
                color = textColor
            )
        }
        badge.invoke()
    }
}
