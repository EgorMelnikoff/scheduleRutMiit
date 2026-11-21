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
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.getColorByIndex
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.EventTimeGroupVo

@Composable
fun ScheduleEventsDetailBadgeWidget(
    eventTimeGroups: List<EventTimeGroupVo>
) {
    val onBackground = AppTheme.colors.onBackground

    val badgeRows = remember(eventTimeGroups) {
        eventTimeGroups.map { timeGroup ->
            timeGroup.events.map { it.tag }
        }
    }

    FlowRow(
        modifier = Modifier.defaultMinSize(minHeight = 6.dp),
        maxItemsInEachRow = if (eventTimeGroups.size in 6..8) 4 else 5,
        horizontalArrangement = Arrangement.spacedBy(
            2.dp,
            Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding2)
    ) {
        badgeRows.forEach { tags ->
            if (tags.isEmpty()) return@forEach

            val canvasWidth = 6.dp + (5.dp * (tags.size - 1))

            Box {
                Canvas(
                    modifier = Modifier.size(width = canvasWidth, height = 6.dp)
                ) {
                    val radius = 3.dp.toPx()
                    val offsetStep = 5.dp.toPx()

                    tags.forEachIndexed { index, tag ->
                        drawCircle(
                            color = tag.getColorByIndex(defaultColor = onBackground),
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
