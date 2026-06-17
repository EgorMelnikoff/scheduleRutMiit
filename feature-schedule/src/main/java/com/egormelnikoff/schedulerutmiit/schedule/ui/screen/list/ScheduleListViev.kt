package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.Empty
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getEnrichedEvents
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.event.Event
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ScheduleState
import java.time.LocalDate


private sealed interface ListEntry {
    data class Header(val date: LocalDate) : ListEntry
    data class EventItem(
        val key: Any,
        val date: LocalDate,
        val enrichedEvents: List<Pair<Event, EventExtraData?>>
    ) : ListEntry
}

@Composable
fun ScheduleListView(
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,

    scheduleListState: LazyListState,
    isSavedSchedule: Boolean,
    namedSchedule: NamedSchedule,
    scheduleState: ScheduleState,

    appSettings: AppSettings,
    paddingBottom: Dp
) {
    val listItems = remember(
        scheduleState.fullEventList,
        scheduleState.eventsExtraData,
        appSettings.eventExtraPolicy
    ) {
        buildList {
            scheduleState.fullEventList.forEach { (date, eventsMap) ->
                add(ListEntry.Header(date))

                eventsMap.entries.forEach { entry ->
                    val enriched = entry.getEnrichedEvents(
                        eventsExtraData = scheduleState.eventsExtraData,
                        eventExtraPolicy = appSettings.eventExtraPolicy,
                        date = date
                    )
                    val firstEvent = entry.value.firstOrNull()
                    val itemKey = if (isSavedSchedule && firstEvent != null) {
                        "${firstEvent.id}_${firstEvent.startDatetime}"
                    } else {
                        entry.hashCode()
                    }

                    add(ListEntry.EventItem(key = itemKey, date = date, enrichedEvents = enriched))
                }
            }
        }
    }

    if (listItems.isNotEmpty()) {
        val navigateToEvent = remember {
            { dialog: Route.Dialog.EventDialog ->
                appBackStack.openDialog(dialog)
            }
        }
        val navigateToEditEvent = remember {
            { dialog: Route.Dialog.AddEditEventDialog ->
                appBackStack.openDialog(dialog)
            }
        }
        LazyColumn(
            state = scheduleListState,
            contentPadding = PaddingValues(bottom = paddingBottom),
            modifier = Modifier.fillMaxSize(),
        ) {
            for (index in listItems.indices) {
                when (val item = listItems[index]) {
                    is ListEntry.Header -> {
                        stickyHeader {
                            DateHeader(
                                date = item.date,
                                formatter = dayMonthNameFormatter
                            )
                        }
                    }

                    is ListEntry.EventItem -> {
                        item {
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Event(
                                    navigateToEvent = navigateToEvent,
                                    navigateToEditEvent = navigateToEditEvent,
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
                                    eventsWithExtra = item.enrichedEvents,
                                    namedScheduleId = namedSchedule.id,
                                    schedule = scheduleState.schedule,
                                    isSavedSchedule = isSavedSchedule,
                                    eventView = appSettings.eventView
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
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