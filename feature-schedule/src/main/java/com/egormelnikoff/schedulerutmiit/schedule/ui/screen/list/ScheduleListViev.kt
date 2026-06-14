package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.Empty
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getEnrichedEvents
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.event.Event
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ScheduleUiDto

@Composable
fun ScheduleListView(
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,

    scheduleListState: LazyListState,
    isSavedSchedule: Boolean,
    namedSchedule: NamedSchedule,
    scheduleUiDto: ScheduleUiDto,

    appSettings: AppSettings,
    paddingBottom: Dp
) {
    if (scheduleUiDto.fullEventList.isNotEmpty()) {
        LazyColumn(
            state = scheduleListState,
            contentPadding = PaddingValues(bottom = paddingBottom),
            modifier = Modifier.fillMaxSize(),
        ) {
            scheduleUiDto.fullEventList.forEach { events ->
                stickyHeader {
                    DateHeader(
                        date = events.key,
                        formatter = dayMonthNameFormatter
                    )
                }
                items(
                    items = events.value.entries.toTypedArray(),
                    key = if (isSavedSchedule) {
                        {
                            val firstEvent = it.value.first()
                            firstEvent.id to firstEvent.startDatetime
                        }
                    } else null
                ) { eventsGrouped ->
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Event(
                            navigateToEvent = { eventDialog ->
                                appBackStack.openDialog(eventDialog)
                            },
                            navigateToEditEvent = { editEventDialog ->
                                appBackStack.openDialog(editEventDialog)
                            },
                            onDeleteEvent = { schedule, eventId ->
                                scheduleViewModel.eventAction(
                                    schedule,
                                    EventAction.Delete(eventId)
                                )
                            },
                            onUpdateHiddenEvent = { schedule, eventId ->
                                scheduleViewModel.eventAction(
                                    schedule,
                                    EventAction.UpdateHidden(eventId, true)
                                )
                            },
                            eventsWithExtra = eventsGrouped.getEnrichedEvents(
                                eventsExtraData = scheduleUiDto.eventsExtraData,
                                eventExtraPolicy = appSettings.eventExtraPolicy,
                                date = events.key
                            ),
                            namedScheduleId = namedSchedule.id,
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