package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.event

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
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.EventView
import java.time.LocalDate

@Composable
fun Event(
    eventsWithExtra: List<Pair<Event, EventExtraData?>>,
    schedule: Schedule,
    isSavedSchedule: Boolean,
    eventView: EventView,
    date: LocalDate,
    navigateToEvent: (Route.Dialog.EventDialog) -> Unit,
    navigateToEditEvent: (Route.Dialog.EditEventDialog) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onUpdateHiddenEvent: (Long) -> Unit
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
                        isSavedSchedule = isSavedSchedule,
                        event = event.first,
                        eventExtraData = event.second,
                        eventDialog = Route.Dialog.EventDialog(
                            eventId = event.first.id,
                            date = date
                        ),
                        editEventDialog = Route.Dialog.EditEventDialog(
                            eventId = event.first.id,
                            scheduleId = schedule.id
                        ),
                        eventView = eventView
                    )
                }
            }
        )
    }
}