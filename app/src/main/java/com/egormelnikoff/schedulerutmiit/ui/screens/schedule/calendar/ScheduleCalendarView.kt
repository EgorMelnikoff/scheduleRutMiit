package com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calendar

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.extension.getEnrichedEvents
import com.egormelnikoff.schedulerutmiit.app.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.app.extension.replaceDate
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.data.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.EventAction
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.Event
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto.ScheduleUiDto

@Composable
fun ScheduleCalendarView(
    scheduleViewModel: ScheduleViewModel,
    appUiState: AppUiState,
    namedSchedule: NamedSchedule,
    scheduleUiDto: ScheduleUiDto,
    isSavedSchedule: Boolean,
    scheduleUiState: ScheduleUiState,
    appSettings: AppSettings,
    paddingBottom: Dp
) {
    Column {
        HorizontalCalendar(
            scheduleUiDto = scheduleUiDto,
            scope = appUiState.scope,
            scheduleUiState = scheduleUiState,
            eventsCountView = appSettings.eventsCountView,
            eventExtraPolicy = appSettings.eventExtraPolicy
        )
        PagedDays(
            scheduleViewModel = scheduleViewModel,
            appBackStack = appUiState.appBackStack,

            namedSchedule = namedSchedule,
            scheduleUiDto = scheduleUiDto,
            pagerDaysState = scheduleUiState.pagerDaysState,

            isSavedSchedule = isSavedSchedule,
            appSettings = appSettings,
            paddingBottom = paddingBottom
        )
    }
}


@Composable
fun PagedDays(
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,

    namedSchedule: NamedSchedule,
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
        val currentDate = scheduleUiDto.scheduleEntity.startDate.plusDays(index.toLong())

        val enrichedEvents by remember(namedSchedule, scheduleUiDto) {
            mutableStateOf(
                currentDate
                    .getEventsForDate(
                        scheduleEntity = scheduleUiDto.scheduleEntity,
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
                        navigateToEvent = { scheduleEntity, isSavedSchedule, event, eventExtraData ->
                            appBackStack.openDialog(
                                Route.Dialog.EventDialog(
                                    namedScheduleEntity = namedSchedule.namedScheduleEntity,
                                    scheduleEntity = scheduleEntity,
                                    isSavedSchedule = isSavedSchedule,
                                    event = event,
                                    dateTime = event.startDatetime.replaceDate(currentDate),
                                    eventExtraData = eventExtraData
                                )
                            )
                        },
                        navigateToEditEvent = { scheduleEntity, event ->
                            appBackStack.openDialog(
                                Route.Dialog.AddEventDialog(
                                    namedSchedule.namedScheduleEntity, scheduleEntity, event
                                )
                            )
                        },
                        onDeleteEvent = { scheduleEntity, eventId ->
                            scheduleViewModel.eventAction(
                                scheduleEntity,
                                eventId,
                                EventAction.Delete
                            )
                        },
                        onUpdateHiddenEvent = { scheduleEntity, event ->
                            scheduleViewModel.eventAction(
                                scheduleEntity,
                                event,
                                EventAction.UpdateHidden(true)
                            )
                        },

                        eventsWithExtra = events.second,
                        scheduleEntity = scheduleUiDto.scheduleEntity,
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