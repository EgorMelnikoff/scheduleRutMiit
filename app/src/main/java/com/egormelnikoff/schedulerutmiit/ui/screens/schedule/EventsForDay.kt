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
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.EventAction
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.ScheduleViewModel
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun EventsForDay(
    appBackStack: AppBackStack,
    scheduleViewModel: ScheduleViewModel,

    namedScheduleEntity: NamedScheduleEntity,
    scheduleEntity: ScheduleEntity,
    date: LocalDate,
    eventsForDate: List<Pair<String, List<Event>>>?,
    eventsExtraData: List<EventExtraData>,
    isSavedSchedule: Boolean,
    appSettings: AppSettings,
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
        if (!eventsForDate.isNullOrEmpty()) {
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
                                namedScheduleEntity = namedScheduleEntity,
                                scheduleEntity = scheduleEntity,
                                isSavedSchedule = isSavedSchedule,
                                event = event,
                                dateTime = LocalDateTime.of(date, event.startDatetime.toLocalTime()),
                                eventExtraData = eventExtraData
                            )
                        )
                    },
                    navigateToEditEvent = { scheduleEntity, event ->
                        appBackStack.openDialog(
                            Route.Dialog.AddEventDialog(
                                namedScheduleEntity, scheduleEntity, event
                            )
                        )
                    },
                    onDeleteEvent = { scheduleEntity, eventId ->
                        scheduleViewModel.eventAction(scheduleEntity, eventId, EventAction.Delete)
                    },
                    onUpdateHiddenEvent = { scheduleEntity, event ->
                        scheduleViewModel.eventAction(scheduleEntity, event, EventAction.UpdateHidden(true))
                    },

                    events = events.second,
                    date = date,
                    scheduleEntity = scheduleEntity,
                    eventsExtraData = eventsExtraData,
                    isSavedSchedule = isSavedSchedule,
                    eventExtraPolicy = appSettings.eventExtraPolicy,
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