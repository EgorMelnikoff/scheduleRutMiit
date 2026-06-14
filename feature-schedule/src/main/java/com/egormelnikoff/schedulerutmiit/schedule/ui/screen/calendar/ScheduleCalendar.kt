package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.core.ui.elements.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.Calendar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.Empty
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getEnrichedEvents
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.calendar.top_bar.ScheduleCalendarTopBar
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.event.Event
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.AppUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ScheduleUiDto
import java.time.LocalDateTime

@Composable
fun ScheduleCalendar(
    scheduleViewModel: ScheduleViewModel,
    appUiState: AppUiState,
    namedScheduleWithSchedules: NamedScheduleWithSchedules,
    scheduleUiDto: ScheduleUiDto,
    hourlyDateTime: LocalDateTime,
    isSavedSchedule: Boolean,
    scheduleCalendarState: CalendarState,
    appSettings: AppSettings,
    paddingBottom: Dp
) {
    var showCalendarDialog by remember { mutableStateOf(false) }

    Column {
        ScheduleCalendarTopBar(
            scheduleUiDto = scheduleUiDto,
            today = hourlyDateTime,
            scheduleCalendarState = scheduleCalendarState,
            eventsCountView = appSettings.eventsCountView,
            eventExtraPolicy = appSettings.eventExtraPolicy,
            showCalendarDialog = showCalendarDialog,
            onShowCalendarDialog = {
                showCalendarDialog = it
            }
        )

        Calendar(
            pagerDaysState = scheduleCalendarState.pagerDaysState
        ) { index ->
            val currentDate = scheduleUiDto.schedule.startDate.plusDays(index.toLong())

            val enrichedEvents by remember(
                namedScheduleWithSchedules.namedSchedule.id,
                scheduleUiDto.schedule,
                scheduleUiDto.hiddenEvents.size
            ) {
                mutableStateOf(
                    scheduleUiDto.schedule
                        .getEventsForDate(
                            date = currentDate,
                            periodicEvents = scheduleUiDto.periodicEvents,
                            nonPeriodicEvents = scheduleUiDto.nonPeriodicEvents
                        )
                        .getEnrichedEvents(
                            eventsExtraData = scheduleUiDto.eventsExtraData,
                            eventExtraPolicy = appSettings.eventExtraPolicy,
                            date = currentDate,
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
                            navigateToEvent = { eventDialog ->
                                appUiState.appBackStack.openDialog(
                                    eventDialog
                                )
                            },
                            navigateToEditEvent = { editEventDialog ->
                                appUiState.appBackStack.openDialog(editEventDialog)
                            },
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
                            schedule = scheduleUiDto.schedule,
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
    }

    if (showCalendarDialog) {
        BottomSheetDatePicker(
            selectedDate = scheduleCalendarState.selectedDate,
            onDateSelect = scheduleCalendarState.onSelectDate,
            startDate = scheduleUiDto.schedule.startDate,
            endDate = scheduleUiDto.schedule.endDate,
            onShowDialog = {
                showCalendarDialog = it
            }
        )
    }
}

@Composable
fun scheduleCalendarState(
    namedScheduleState: NamedScheduleState,
    scheduleState: ScheduleState
): CalendarState? {
    return if (scheduleState.scheduleUiDto?.calendarData != null) {
        val pagerDaysState = rememberPagerState(
            pageCount = { scheduleState.scheduleUiDto.calendarData.daysCount },
            initialPage = scheduleState.scheduleUiDto.calendarData.daysPagerDefaultIndex
        )
        val pagerWeeksState = rememberPagerState(
            pageCount = { scheduleState.scheduleUiDto.calendarData.weeksCount },
            initialPage = scheduleState.scheduleUiDto.calendarData.weeksPagerDefaultIndex
        )

        var selectedDate by remember(
            namedScheduleState.namedScheduleWithSchedules?.namedSchedule?.apiId
        ) {
            mutableStateOf(
                scheduleState.scheduleUiDto.calendarData.defaultDate
            )
        }

        CalendarState(
            pagerWeeksState = pagerWeeksState,
            pagerDaysState = pagerDaysState,
            selectedDate = selectedDate,
            onSelectDate = { newDate ->
                selectedDate = newDate
            }
        )
    } else null
}