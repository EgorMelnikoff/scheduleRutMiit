package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.getColorByIndex
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.findEventExtra
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.EventExtraData
import java.time.LocalDate

@Composable
fun ScheduleEventsDetailBadgeWidget(
    currentDate: LocalDate,
    events: Map<String, List<Event>>,
    eventsExtraData: Map<Long, List<EventExtraData>>,
    eventExtraPolicy: EventExtraPolicy
) {
    val onBackground = AppTheme.colors.onBackground

    val badgeRows = remember(events, eventsExtraData, eventExtraPolicy, currentDate) {
        events.map { (_, groupedEvents) ->
            groupedEvents.map { event ->
                val extra = eventsExtraData.findEventExtra(
                    eventExtraPolicy = eventExtraPolicy,
                    eventId = event.id,
                    date = currentDate
                )
                extra?.tag.getColorByIndex(defaultColor = onBackground)
            }
        }
    }

    FlowRow(
        modifier = Modifier.defaultMinSize(minHeight = 6.dp),
        maxItemsInEachRow = if (events.size in 6..8) 4 else 5,
        horizontalArrangement = Arrangement.spacedBy(
            2.dp,
            Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding2)
    ) {
        badgeRows.forEach { colors ->
            if (colors.isEmpty()) return@forEach

            val canvasWidth = 6.dp + (5.dp * (colors.size - 1))

            Box {
                Canvas(
                    modifier = Modifier.size(width = canvasWidth, height = 6.dp)
                ) {
                    val radius = 3.dp.toPx()
                    val offsetStep = 5.dp.toPx()

                    colors.forEachIndexed { index, color ->
                        drawCircle(
                            color = color,
                            radius = radius,
                            center = Offset(
                                x = radius + (index * offsetStep),
                                y = center.y
                            )
                        )
                    }
                }
            }
        }
    }
}
