package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.core.common.extension.replaceDate
import com.egormelnikoff.schedulerutmiit.core.ui.elements.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.Empty
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getEnrichedEvents
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.event.Event
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.AppUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ScheduleUiDto

@Composable
fun ScheduleCalendarView(
    scheduleViewModel: ScheduleViewModel,
    appUiState: AppUiState,
    namedScheduleWithSchedules: NamedScheduleWithSchedules,
    scheduleUiDto: ScheduleUiDto,
    isSavedSchedule: Boolean,
    scheduleUiState: ScheduleUiState,
    appSettings: AppSettings,
    paddingBottom: Dp
) {
    var showCalendarDialog by remember { mutableStateOf(false) }

    Column {
        HorizontalCalendar(
            scheduleUiDto = scheduleUiDto,
            scope = appUiState.scope,
            scheduleUiState = scheduleUiState,
            eventsCountView = appSettings.eventsCountView,
            eventExtraPolicy = appSettings.eventExtraPolicy,
            showCalendarDialog = showCalendarDialog,
            onShowCalendarDialog = {
                showCalendarDialog = it
            }
        )
        PagedDays(
            scheduleViewModel = scheduleViewModel,
            appBackStack = appUiState.appBackStack,

            namedScheduleWithSchedules = namedScheduleWithSchedules,
            scheduleUiDto = scheduleUiDto,
            pagerDaysState = scheduleUiState.pagerDaysState,

            isSavedSchedule = isSavedSchedule,
            appSettings = appSettings,
            paddingBottom = paddingBottom
        )
    }

    if (showCalendarDialog) {
        BottomSheetDatePicker(
            selectedDate = scheduleUiState.selectedDate,
            onDateSelect = scheduleUiState.onSelectDate,
            startDate = scheduleUiDto.schedule.startDate,
            endDate = scheduleUiDto.schedule.endDate,
            onShowDialog = {
                showCalendarDialog = it
            }
        )
    }
}


@Composable
fun PagedDays(
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,

    namedScheduleWithSchedules: NamedScheduleWithSchedules,
    scheduleUiDto: ScheduleUiDto,
    pagerDaysState: PagerState,

    isSavedSchedule: Boolean,
    appSettings: AppSettings,
    paddingBottom: Dp
) {
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerDaysState,
        verticalAlignment = Alignment.Top,
        pageSpacing = 12.dp
    ) { index ->
        val currentDate = scheduleUiDto.schedule.startDate.plusDays(index.toLong())

        val enrichedEvents by remember(
            namedScheduleWithSchedules,
            scheduleUiDto.schedule,
            scheduleUiDto.fullEventList
        ) {
            mutableStateOf(
                scheduleUiDto.schedule
                    .getEventsForDate(
                        date = currentDate,
                        periodicEvents = scheduleUiDto.periodicEvents,
                        nonPeriodicEvents = scheduleUiDto.nonPeriodicEvents
                    )
                    .toList()
                    .getEnrichedEvents(
                        date = currentDate,
                        eventsExtraData = scheduleUiDto.eventsExtraData,
                        eventExtraPolicy = appSettings.eventExtraPolicy
                    )
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
                    key = if (isSavedSchedule) {
                        { it.second.first().first.id }
                    } else null
                ) { events ->
                    Event(
                        navigateToEvent = { schedule, isSavedSchedule, event, eventExtraData ->
                            appBackStack.openDialog(
                                Route.Dialog.EventDialog(
                                    namedSchedule = namedScheduleWithSchedules.namedSchedule,
                                    schedule = schedule,
                                    isSavedSchedule = isSavedSchedule,
                                    event = event,
                                    dateTime = event.startDatetime.replaceDate(currentDate),
                                    eventExtraData = eventExtraData
                                )
                            )
                        },
                        navigateToEditEvent = { schedule, event ->
                            appBackStack.openDialog(
                                Route.Dialog.AddEventDialog(
                                    namedScheduleWithSchedules.namedSchedule, schedule, event
                                )
                            )
                        },
                        onDeleteEvent = { schedule, eventId ->
                            scheduleViewModel.eventAction(
                                schedule,
                                eventId,
                                EventAction.Delete
                            )
                        },
                        onUpdateHiddenEvent = { schedule, event ->
                            scheduleViewModel.eventAction(
                                schedule,
                                event,
                                EventAction.UpdateHidden(true)
                            )
                        },

                        eventsWithExtra = events.second,
                        schedule = scheduleUiDto.schedule,
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
}