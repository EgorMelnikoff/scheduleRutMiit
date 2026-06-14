package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.calendar.top_bar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.extension.replaceDate
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.getColorByIndex
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.findEventExtra
import java.time.LocalDate
import kotlin.collections.forEach

@Composable
fun EventsDetailBadge(
    currentDate: LocalDate,
    events: Map<String, List<Event>>,
    eventsExtraData: Map<Long, EventExtraData>,
    eventExtraPolicy: EventExtraPolicy
) {
    FlowRow(
        modifier = Modifier.defaultMinSize(minHeight = 6.dp),
        maxItemsInEachRow = if (events.size in 6..8) 4 else 5,
        horizontalArrangement = Arrangement.spacedBy(
            2.dp,
            Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        events.forEach { groupedEvents ->
            var offset = 0
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    groupedEvents.value.forEach { event ->
                        val eventExtraData = eventsExtraData.findEventExtra(
                            eventExtraPolicy = eventExtraPolicy,
                            eventId = event.id,
                            dateTime = event.startDatetime.replaceDate(currentDate)
                        )
                        val color = eventExtraData?.tag.getColorByIndex(
                            defaultColor = MaterialTheme.colorScheme.onBackground
                        )
                        Canvas(
                            modifier = Modifier
                                .padding(start = offset.dp)
                                .size(6.dp)
                        ) {
                            drawCircle(
                                color = color,
                                center = center
                            )
                        }
                        offset += 5
                    }
                }
            }
        }
    }
}
