package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.preferences.EventView
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel

@Composable
fun EventsForDay(
    appBackStack: AppBackStack,
    scheduleViewModel: ScheduleViewModel,

    scheduleEntity: ScheduleEntity,
    namedScheduleData: NamedScheduleData,

    eventsForDate: List<Pair<String, List<Event>>>,
    isSavedSchedule: Boolean,
    eventView: EventView,
    paddingBottom: Dp
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = paddingBottom
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (eventsForDate.isNotEmpty()) {
            items(
                items = eventsForDate,
                key = if (isSavedSchedule) {
                    { it.second.first().id }
                } else null
            ) { events ->
                Event(
                    navigateToEvent = { scheduleEntity, isSavedSchedule, event, eventExtraData ->
                        appBackStack.openDialog(
                            Route.Dialog.EventDialog(
                                namedScheduleEntity = namedScheduleData.namedSchedule!!.namedScheduleEntity,
                                scheduleEntity = scheduleEntity,
                                isSavedSchedule = isSavedSchedule,
                                event = event,
                                eventExtraData = eventExtraData
                            )
                        )
                    },
                    navigateToEditEvent = { scheduleEntity, event ->
                        appBackStack.openDialog(
                            Route.Dialog.AddEventDialog(
                                namedScheduleData.namedSchedule!!.namedScheduleEntity, scheduleEntity, event
                            )
                        )
                    },
                    onDeleteEvent = { scheduleEntity, eventId ->
                        scheduleViewModel.deleteCustomEvent(scheduleEntity, eventId)
                    },
                    onUpdateHiddenEvent = { scheduleEntity, eventId ->
                        scheduleViewModel.updateEventHidden(scheduleEntity, eventId, true)
                    },

                    events = events.second,
                    scheduleEntity = scheduleEntity,
                    eventsExtraData = namedScheduleData.scheduleData!!.eventsExtraData,

                    isSavedSchedule = isSavedSchedule,
                    eventView = eventView
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