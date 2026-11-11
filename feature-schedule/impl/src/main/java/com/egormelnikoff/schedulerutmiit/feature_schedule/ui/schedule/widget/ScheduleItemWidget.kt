package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventView
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import java.time.LocalDate

@Composable
fun ScheduleItemWidget(
    eventsWithExtra: List<Pair<Event, EventExtraData?>>,
    schedule: Schedule,
    isSavedSchedule: Boolean,
    eventView: EventView,
    date: LocalDate?,
    navigateToEvent: (Route.Dialog.EventDialog) -> Unit,
    navigateToEditEvent: (Route.Dialog.EditEventDialog) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onHideEvent: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
    ) {
        Row(
            modifier = Modifier.padding(vertical = AppTheme.paddings.padding4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (eventsWithExtra.first().first.timeSlotName != null) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = eventsWithExtra.first().first.timeSlotName.toString(),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.primary
                )
            }
            Text(
                text =
                    "${
                        eventsWithExtra.first().first.startDatetime.toLocalTime()
                    } - ${
                        eventsWithExtra.first().first.endDatetime.toLocalTime()
                    }",
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.primary
            )
        }

        ColumnGroup(
            items = eventsWithExtra.map { event ->
                {
                    ScheduleItemSingleWidget(
                        navigateToEvent = navigateToEvent,
                        navigateToEditEvent = navigateToEditEvent,
                        onDeleteEvent = onDeleteEvent,
                        onUpdateHiddenEvent = onHideEvent,
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