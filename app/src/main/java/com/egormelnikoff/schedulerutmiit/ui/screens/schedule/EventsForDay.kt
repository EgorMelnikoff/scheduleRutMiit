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
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.state.actions.EventActions
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData

@Composable
fun EventsForDay(
    navigationActions: NavigationActions,
    eventActions: EventActions,

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
                items = eventsForDate
            ) { events ->
                Event(
                    navigateToEvent = navigationActions.navigateToEvent,
                    navigateToEditEvent = navigationActions.navigateToEditEvent,
                    onDeleteEvent = eventActions.onDeleteEvent,
                    onUpdateHiddenEvent = eventActions.onHideEvent,

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