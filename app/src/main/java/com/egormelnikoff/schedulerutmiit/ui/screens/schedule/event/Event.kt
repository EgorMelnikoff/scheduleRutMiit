package com.egormelnikoff.schedulerutmiit.ui.screens.schedule.event

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
import com.egormelnikoff.egormelnikoff.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.common.preferences.EventView
import com.egormelnikoff.schedulerutmiit.core.database.entity.Event
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.database.entity.ScheduleEntity

@Composable
fun Event(
    eventsWithExtra: List<Pair<Event, EventExtraData?>>,
    scheduleEntity: ScheduleEntity,
    isSavedSchedule: Boolean,
    eventView: EventView,
    navigateToEvent: (ScheduleEntity, Boolean, Event, EventExtraData?) -> Unit,
    navigateToEditEvent: (ScheduleEntity, Event) -> Unit,
    onDeleteEvent: (ScheduleEntity, Event) -> Unit,
    onUpdateHiddenEvent: (ScheduleEntity, Event) -> Unit
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
                        scheduleEntity = scheduleEntity,
                        eventExtraData = event.second,
                        isSavedSchedule = isSavedSchedule,
                        eventView = eventView
                    )
                }
            }
        )
    }
}