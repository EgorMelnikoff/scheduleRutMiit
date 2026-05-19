package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.extension.replaceDate
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.Empty
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.findEventExtra
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getGroupedEvents
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.event.Event
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ScheduleUiDto

@Composable
fun ScheduleListView(
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,

    scheduleUiState: ScheduleUiState,

    isSavedSchedule: Boolean,
    namedSchedule: NamedSchedule,
    scheduleUiDto: ScheduleUiDto,

    appSettings: AppSettings,
    paddingBottom: Dp
) {
    if (scheduleUiDto.fullEventList.isNotEmpty()) {
        LazyColumn(
            state = scheduleUiState.scheduleListState,
            contentPadding = PaddingValues(bottom = paddingBottom),
            modifier = Modifier.fillMaxSize(),
        ) {
            scheduleUiDto.fullEventList.forEach { events ->
                val eventsForDay = events.second.getGroupedEvents().toList()
                stickyHeader {
                    DateHeader(
                        date = events.first,
                        formatter = dayMonthNameFormatter
                    )
                }

                items(
                    items = eventsForDay,
                    key = if (isSavedSchedule) {
                        {
                            Pair(
                                it.second.first().id,
                                it.second.first().startDatetime
                            )
                        }
                    } else null
                ) { eventsGrouped ->
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Event(
                            navigateToEvent = { schedule, isSavedSchedule, event, eventExtraData ->
                                appBackStack.openDialog(
                                    Route.Dialog.EventDialog(
                                        namedSchedule = namedSchedule,
                                        schedule = schedule,
                                        isSavedSchedule = isSavedSchedule,
                                        dateTime = event.startDatetime,
                                        event = event,
                                        eventExtraData = eventExtraData
                                    )
                                )
                            },
                            navigateToEditEvent = { schedule, event ->
                                appBackStack.openDialog(
                                    Route.Dialog.AddEventDialog(
                                        namedSchedule, schedule, event
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
                            eventsWithExtra = eventsGrouped.second.map { event ->
                                event to scheduleUiDto.eventsExtraData.findEventExtra(
                                    eventExtraPolicy = appSettings.eventExtraPolicy,
                                    event = event,
                                    dateTime = event.startDatetime.replaceDate(events.first)
                                )
                            },
                            schedule = scheduleUiDto.schedule,
                            isSavedSchedule = isSavedSchedule,
                            eventView = appSettings.eventView
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    } else {
        Empty(
            title = "¯\\_(ツ)_/¯",
            subtitle = stringResource(R.string.no_classes),
            isBoldTitle = false,
            paddingBottom = paddingBottom
        )
    }
}