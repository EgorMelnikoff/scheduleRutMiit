package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.schedule.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.AppSettings
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.ui.widget.EmptyScreen
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Event
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.utils.extension.getEnrichedEvents
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.schedule.ScheduleState
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
fun ScheduleListWidget(
    scheduleListState: LazyListState,
    isSavedSchedule: Boolean,
    scheduleState: ScheduleState,

    appSettings: AppSettings,
    paddingBottom: Dp,
    onOpenDialog: (Route.Dialog) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onHideEvent: (Long) -> Unit
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
                onOpenDialog(dialog)
            }
        }
        val navigateToEditEvent = remember {
            { dialog: Route.Dialog.EditEventDialog ->
                onOpenDialog(dialog)
            }
        }
        LazyColumn(
            state = scheduleListState,
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                bottom = paddingBottom
            ),
            modifier = Modifier.fillMaxSize(),
        ) {
            listItems.forEach { item ->
                when (item) {
                    is ListEntry.Header -> {
                        scheduleListStickyHeaderWidget(
                            item.date, dayMonthNameFormatter
                        )
                    }

                    is ListEntry.EventItem -> {
                        item {
                            ScheduleItemWidget(
                                navigateToEvent = navigateToEvent,
                                navigateToEditEvent = navigateToEditEvent,
                                onDeleteEvent = onDeleteEvent,
                                onHideEvent = onHideEvent,

                                date = if (appSettings.eventExtraPolicy == EventExtraPolicy.BY_DATES) {
                                    item.date
                                } else null,
                                eventsWithExtra = item.enrichedEvents,
                                schedule = scheduleState.schedule,
                                isSavedSchedule = isSavedSchedule,
                                eventView = appSettings.eventView
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    } else {
        EmptyScreen(
            title = "¯\\_(ツ)_/¯",
            subtitle = stringResource(R.string.no_classes),
            isBoldTitle = false,
            paddingBottom = paddingBottom
        )
    }
}