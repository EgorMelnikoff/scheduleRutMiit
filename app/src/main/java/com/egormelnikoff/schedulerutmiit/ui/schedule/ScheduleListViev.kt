package com.egormelnikoff.schedulerutmiit.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.Event
import com.egormelnikoff.schedulerutmiit.data.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.composable.Empty
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

@Composable
fun ScheduleListView(
    onShowDialogEvent: (Boolean) -> Unit,
    onSelectDisplayedEvent: (Event) -> Unit,
    scheduleListState: LazyListState,
    isShortEvent: Boolean,
    eventsByWeekAndDays: MutableMap<Int, Map<LocalDate, List<Event>>>,
    eventsExtraData: List<EventExtraData>,
    scheduleEntity: ScheduleEntity,
    today: LocalDate
) {
    val eventsGrouped by remember(
        scheduleEntity.namedScheduleId,
        scheduleEntity.id
    ) {
        mutableStateOf(
            calculateEvents(
                eventsByWeekAndDays = eventsByWeekAndDays,
                today = today,
                scheduleEntity = scheduleEntity
            )
                .filter { it.startDatetime!!.toLocalDate() >= today }
                .sortedBy { it.startDatetime }
                .groupBy { event ->
                    event.startDatetime!!.toLocalDate()
                }
        )
    }

    if (eventsGrouped.isNotEmpty()) {
        LazyColumn(
            state = scheduleListState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            val formatter = DateTimeFormatter.ofPattern("d MMMM")
            eventsGrouped.forEach { events ->
                stickyHeader {
                    DateHeader(events.key, formatter)
                }
                val eventsForDayGrouped = events.value
                    .sortedBy { event -> event.startDatetime!!.toLocalTime() }
                    .groupBy { event ->
                        event.startDatetime.toString()
                    }
                    .toList()
                items(eventsForDayGrouped) { eventsGrouped ->
                    Event(
                        isShortEvent = isShortEvent,
                        eventsExtraData = eventsExtraData,
                        events = eventsGrouped.second,
                        onShowDialogEvent = onShowDialogEvent,
                        onSelectDisplayedEvent = onSelectDisplayedEvent
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    } else {
        Empty(
            title = "¯\\_(ツ)_/¯",
            subtitle = LocalContext.current.getString(R.string.no_classes),
            isBoldTitle = false
        )
    }
}

@Composable
fun DateHeader(date: LocalDate, formatter: DateTimeFormatter) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.width(16.dp),
            imageVector = ImageVector.vectorResource(R.drawable.calendar),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "${
                date.dayOfWeek.getDisplayName(
                    TextStyle.SHORT_STANDALONE,
                    Locale.getDefault()
                ).replaceFirstChar { it.uppercase() }
            }, ${formatter.format(date)}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

fun calculateEvents(
    eventsByWeekAndDays: MutableMap<Int, Map<LocalDate, List<Event>>>,
    today: LocalDate,
    scheduleEntity: ScheduleEntity
): MutableList<Event> {
    val displayedEvents = mutableListOf<Event>()
    if (scheduleEntity.recurrence != null) {
        val weeksRemaining = abs(
            ChronoUnit.WEEKS.between(
                today,
                scheduleEntity.endDate
            )
        ).toInt().plus(1)
        for (week in 1..weeksRemaining) {
            val weekNumber =
                ((week + scheduleEntity.recurrence.firstWeekNumber) % scheduleEntity.recurrence.interval!!).plus(
                    1
                )
            val events = eventsByWeekAndDays[weekNumber]?.values?.flatten()
            if (events != null) {
                for (event in events) {
                    val startOfWeek = calculateFirstDayOfWeek(today)
                        .plusDays(event.startDatetime!!.toLocalDate().dayOfWeek.value - 1L)
                        .plusWeeks(week - 1L)

                    val newEvent = event.copy(
                        startDatetime = startOfWeek.atTime(event.startDatetime.toLocalTime()),
                        endDatetime = startOfWeek.atTime(event.endDatetime?.toLocalTime())
                    )

                    displayedEvents.add(newEvent)
                }
            }
        }
    } else {
        if (!eventsByWeekAndDays[1]?.values.isNullOrEmpty()) {
            displayedEvents.addAll(eventsByWeekAndDays[1]?.values!!.flatten())
        }
    }
    return displayedEvents
}