package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.ui.components.badge.AppBadge
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.Calendar
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.CalendarBarItem
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.bottom_sheet.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.widget.EmptyScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.ScheduleVo
import java.time.LocalDate

@Composable
fun ScheduleCalendarWidget(
    scheduleVo: ScheduleVo,
    isSavedSchedule: Boolean,
    scheduleCalendarState: CalendarState,
    today: LocalDate,
    eventsCountView: EventsCountView,
    eventView: EventView,
    paddingBottom: Dp,
    onAction: (ScheduleAction) -> Unit
) {
    var showCalendarDialog by remember { mutableStateOf(false) }

    Calendar(
        calendarState = scheduleCalendarState,
        showCalendarDialog = showCalendarDialog,
        showMonth = true,
        onShowCalendarDialog = {
            showCalendarDialog = it
        },
        monthBadge = { firstDayOfCurrentWeek ->
            val recurrence = scheduleVo.schedule.recurrence
            if (recurrence != null && recurrence.interval > 1) {
                val selectedWeek = scheduleVo.weekByFirstDayOfWeek[firstDayOfCurrentWeek] ?: 0

                Icon(
                    modifier = Modifier.size(3.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.circle),
                    contentDescription = null,
                    tint = AppTheme.colors.onSecondaryContainer
                )
                Text(
                    text = stringResource(
                        R.string.week,
                        selectedWeek.toString()
                    ).replaceFirstChar { it.lowercase() },
                    style = AppTheme.typography.bodyLarge,
                    color = AppTheme.colors.onBackground,
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(AppTheme.colors.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (scheduleCalendarState.currentWeekPage + 1).toString(),
                        fontSize = 12.sp,
                        style = AppTheme.typography.bodyLarge,
                        color = AppTheme.colors.onBackground
                    )
                }
            }
        },
        calendarBarItem = { _, currentDate ->
            val timeGroups = scheduleVo.eventsByDate[currentDate].orEmpty()

            CalendarBarItem(
                currentDate = currentDate,

                isSelected = scheduleCalendarState.selectedDate == currentDate,
                isDisabled = currentDate !in scheduleVo.schedule.startDate..scheduleVo.schedule.endDate,
                isToday = today == currentDate,

                selectDate = { date ->
                    scheduleCalendarState.selectDate(date, timeGroups.isEmpty())
                }
            ) {
                when {
                    eventsCountView == EventsCountView.DETAILS && timeGroups.isNotEmpty() -> {
                        ScheduleEventsDetailBadgeWidget(
                            eventTimeGroups = timeGroups
                        )
                    }

                    eventsCountView == EventsCountView.BRIEFLY && timeGroups.isNotEmpty() -> {
                        AppBadge(
                            count = scheduleVo.eventsCountByDate[currentDate] ?: 0
                        )
                    }
                }
            }
        }
    ) { _, currentDate ->
        val timeGroups = scheduleVo.eventsByDate[currentDate].orEmpty()

        LazyColumn(
            contentPadding = PaddingValues(
                start = AppTheme.paddings.padding16,
                end = AppTheme.paddings.padding16,
                bottom = paddingBottom
            ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
        ) {
            if (timeGroups.isNotEmpty()) {
                items(
                    items = timeGroups,
                    key = { timeGroup ->
                        if (isSavedSchedule) timeGroup.events.first().id else timeGroup.hashCode()
                    }
                ) { timeGroup ->
                    ScheduleItemWidget(
                        timeGroup = timeGroup,
                        eventView = eventView,
                        isSavedSchedule = isSavedSchedule,
                        onAction = onAction
                    )
                }
            } else {
                item {
                    EmptyScreen(
                        modifier = Modifier.fillParentMaxSize(),
                        title = stringResource(R.string.day_off),
                        subtitle = stringResource(R.string.empty_day)
                    )
                }
            }
        }
    }

    if (showCalendarDialog) {
        BottomSheetDatePicker(
            selectedDate = scheduleCalendarState.selectedDate,
            onDateSelect = { date ->
                scheduleCalendarState.selectDate(date)
            },
            startDate = scheduleVo.schedule.startDate,
            endDate = scheduleVo.schedule.endDate,
            onShowDialog = {
                showCalendarDialog = it
            }
        )
    }
}
