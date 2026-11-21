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
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventView
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.EventTimeGroupVo

@Composable
fun ScheduleItemWidget(
    timeGroup: EventTimeGroupVo,
    eventView: EventView,
    isSavedSchedule: Boolean,
    onAction: (ScheduleAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
    ) {
        Row(
            modifier = Modifier.padding(vertical = AppTheme.paddings.padding4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (timeGroup.timeSlotName != null) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = timeGroup.timeSlotName,
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.primary
                )
            }
            Text(
                text = "${timeGroup.startTime} - ${timeGroup.endTime}",
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.primary
            )
        }

        ColumnGroup(
            items = timeGroup.events.map { event ->
                {
                    ScheduleItemSingleWidget(
                        event = event,
                        eventView = eventView,
                        isSavedSchedule = isSavedSchedule,
                        onAction = onAction
                    )
                }
            }
        )
    }
}
