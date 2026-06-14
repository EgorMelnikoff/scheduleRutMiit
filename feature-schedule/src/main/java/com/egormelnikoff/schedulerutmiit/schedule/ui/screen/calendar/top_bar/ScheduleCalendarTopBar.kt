package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.calendar.top_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.top_bar.CalendarTopBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getCurrentWeek
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ScheduleUiDto
import java.time.LocalDateTime

@Composable
fun ScheduleCalendarTopBar(
    eventsCountView: EventsCountView,
    eventExtraPolicy: EventExtraPolicy,
    today: LocalDateTime,
    scheduleUiDto: ScheduleUiDto,
    scheduleCalendarState: CalendarState,
    showCalendarDialog: Boolean,
    onShowCalendarDialog: (Boolean) -> Unit
) {
    CalendarTopBar(
        calendarData = scheduleUiDto.calendarData,
        calendarState = scheduleCalendarState,
        showCalendarDialog = showCalendarDialog,
        onShowCalendarDialog = onShowCalendarDialog,
        showMonth = true,
        monthBadge = if (scheduleUiDto.schedule.recurrence != null &&
            requireNotNull(scheduleUiDto.schedule.recurrence).interval > 1
        ) {
            { firstDayOfCurrentWeek ->
                val selectedWeek = firstDayOfCurrentWeek.getCurrentWeek(
                    startDate = scheduleUiDto.schedule.startDate,
                    recurrence = scheduleUiDto.schedule.recurrence
                )
                val color = MaterialTheme.colorScheme.onSecondaryContainer
                Icon(
                    modifier = Modifier.size(3.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.circle),
                    contentDescription = null,
                    tint = color
                )
                Text(
                    text = stringResource(
                        R.string.week,
                        selectedWeek.toString()
                    ).replaceFirstChar { it.lowercase() },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (scheduleCalendarState.pagerWeeksState.currentPage + 1).toString(),
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else null
    ) { _, currentDate ->
        val eventsForDate = scheduleUiDto.schedule.getEventsForDate(
            date = currentDate,
            periodicEvents = scheduleUiDto.periodicEvents,
            nonPeriodicEvents = scheduleUiDto.nonPeriodicEvents
        )

        ScheduleCalendarTopBarItem(
            selectDate = scheduleCalendarState.onSelectDate,
            currentDate = currentDate,
            events = eventsForDate,
            eventsExtraData = scheduleUiDto.eventsExtraData,
            eventsCountView = eventsCountView,
            isDisabled = currentDate !in scheduleUiDto.schedule.startDate..scheduleUiDto.schedule.endDate,
            isSelected = currentDate == scheduleCalendarState.selectedDate,
            isToday = (currentDate == today),
            eventExtraPolicy = eventExtraPolicy
        )
    }
}
