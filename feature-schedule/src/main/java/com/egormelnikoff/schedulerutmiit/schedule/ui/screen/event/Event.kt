package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.EventView
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup

@Composable
fun Event(
    eventsWithExtra: List<Pair<Event, EventExtraData?>>,
    schedule: Schedule,
    isSavedSchedule: Boolean,
    eventView: EventView,
    navigateToEvent: (Schedule, Boolean, Event, EventExtraData?) -> Unit,
    navigateToEditEvent: (Schedule, Event) -> Unit,
    onDeleteEvent: (Schedule, Event) -> Unit,
    onUpdateHiddenEvent: (Schedule, Event) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (eventsWithExtra.first().first.timeSlotName != null) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = eventsWithExtra.first().first.timeSlotName.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text =
                    "${
                        eventsWithExtra.first().first.startDatetime.toLocalTimeWithTimeZone()
                    } - ${
                        eventsWithExtra.first().first.endDatetime.toLocalTimeWithTimeZone()
                    }",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        ColumnGroup(
            items = eventsWithExtra.map { event ->
                {
                    ScheduleSingleEvent(
                        navigateToEvent = navigateToEvent,
                        navigateToEditEvent = navigateToEditEvent,
                        onDeleteEvent = onDeleteEvent,
                        onUpdateHiddenEvent = onUpdateHiddenEvent,
                        event = event.first,
                        schedule = schedule,
                        eventExtraData = event.second,
                        isSavedSchedule = isSavedSchedule,
                        eventView = eventView
                    )
                }
            }
        )
    }
}