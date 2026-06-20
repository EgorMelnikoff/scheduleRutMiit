package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomBadge
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.Calendar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.CalendarBarItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.bottom_sheet.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.Empty
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getCurrentWeek
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getEnrichedEvents
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.elements.EventsDetailBadge
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.event.Event
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.AppUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ScheduleState
import java.time.LocalDateTime

@Composable
fun ScheduleCalendar(
    scheduleViewModel: ScheduleViewModel,
    appUiState: AppUiState,
    namedScheduleWithSchedules: NamedScheduleWithSchedules,
    scheduleState: ScheduleState,
    hourlyDateTime: LocalDateTime,
    isSavedSchedule: Boolean,
    scheduleCalendarState: CalendarState,
    appSettings: AppSettings,
    paddingBottom: Dp
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
            if (scheduleState.schedule.recurrence != null &&
                scheduleState.schedule.recurrence!!.interval > 1
            ) {
                val selectedWeek = firstDayOfCurrentWeek.getCurrentWeek(
                    startDate = scheduleState.schedule.startDate,
                    recurrence = scheduleState.schedule.recurrence
                )
                Icon(
                    modifier = Modifier.size(3.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.circle),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
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
                        text = (scheduleCalendarState.currentWeekPage + 1).toString(),
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        calendarBarItem = { _, currentDate ->
            val eventsForDate = remember(
                scheduleState.schedule,
                scheduleState.fullEventList.size,
                scheduleState.hiddenEvents.size,
                currentDate
            ) {
                scheduleState.schedule.getEventsForDate(
                    date = currentDate,
                    periodicEvents = scheduleState.periodicEvents,
                    nonPeriodicEvents = scheduleState.nonPeriodicEvents
                )
            }

            CalendarBarItem(
                currentDate = currentDate,

                isSelected = scheduleCalendarState.selectedDate == currentDate,
                isDisabled = currentDate !in scheduleState.schedule.startDate..scheduleState.schedule.endDate,
                isToday = (currentDate == hourlyDateTime.toLocalDate()),

                selectDate = { date ->
                    scheduleCalendarState.selectDate(date, eventsForDate.isEmpty())
                }
            ) {
                if (appSettings.eventsCountView == EventsCountView.DETAILS) {
                    EventsDetailBadge(
                        currentDate = currentDate,
                        events = eventsForDate,
                        eventsExtraData = scheduleState.eventsExtraData,
                        eventExtraPolicy = appSettings.eventExtraPolicy
                    )
                } else if (appSettings.eventsCountView == EventsCountView.BRIEFLY && eventsForDate.isNotEmpty()) {
                    CustomBadge(
                        count = eventsForDate.size
                    )
                }
            }
        }

    ) { _, currentDate ->
        val enrichedEvents = remember(
            scheduleState.schedule,
            scheduleState.fullEventList.size,
            scheduleState.hiddenEvents.size,
            currentDate
        ) {
            scheduleState.schedule
                .getEventsForDate(
                    date = currentDate,
                    periodicEvents = scheduleState.periodicEvents,
                    nonPeriodicEvents = scheduleState.nonPeriodicEvents
                )
                .getEnrichedEvents(
                    eventsExtraData = scheduleState.eventsExtraData,
                    eventExtraPolicy = appSettings.eventExtraPolicy,
                    date = currentDate,
                )
        }

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = paddingBottom
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (enrichedEvents.isNotEmpty()) {
                items(
                    items = enrichedEvents,
                    key = { item ->
                        if (isSavedSchedule) item.second.first().first.id else item.hashCode()
                    }
                ) { events ->
                    val navigateToEvent = remember {
                        { dialog: Route.Dialog.EventDialog ->
                            appUiState.appBackStack.openDialog(dialog)
                        }
                    }
                    val navigateToEditEvent = remember {
                        { dialog: Route.Dialog.AddEditEventDialog ->
                            appUiState.appBackStack.openDialog(dialog)
                        }
                    }

                    Event(
                        navigateToEvent = navigateToEvent,
                        navigateToEditEvent = navigateToEditEvent,
                        onDeleteEvent = { namedScheduleId, eventId ->
                            scheduleViewModel.eventAction(
                                namedScheduleId,
                                EventAction.Delete(eventId)
                            )
                        },
                        onUpdateHiddenEvent = { namedScheduleId, eventId ->
                            scheduleViewModel.eventAction(
                                namedScheduleId,
                                EventAction.UpdateHidden(eventId, true)
                            )
                        },

                        eventsWithExtra = events.second,
                        schedule = scheduleState.schedule,
                        date = currentDate,
                        namedScheduleId = namedScheduleWithSchedules.namedSchedule.id,
                        isSavedSchedule = isSavedSchedule,
                        eventView = appSettings.eventView
                    )
                }
            } else {
                item {
                    Empty(
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
            startDate = scheduleState.schedule.startDate,
            endDate = scheduleState.schedule.endDate,
            onShowDialog = {
                showCalendarDialog = it
            }
        )
    }
}