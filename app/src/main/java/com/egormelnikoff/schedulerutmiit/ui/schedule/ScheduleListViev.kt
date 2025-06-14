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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.egormelnikoff.schedulerutmiit.ui.composable.Empty
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

@Composable
fun ScheduleListView(
    showEventDialog: (Event?) -> Unit,
    scheduleState: ScheduleState.Loaded,
    lazyListState: LazyListState,
    isShortEvent: Boolean,
    eventsByWeekAndDays: MutableMap<Int, Map<LocalDate, List<Event>>>,
    today: LocalDate
) {

    val eventsGrouped by remember(scheduleState.selectedSchedule!!.scheduleEntity) {
        mutableStateOf(
            calculateEvents(
                eventsByWeekAndDays = eventsByWeekAndDays,
                today = today,
                scheduleState = scheduleState
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
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = 12.dp),
            modifier = Modifier.fillMaxSize()
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
                        isShowPriority = true,
                        isShortEvent = isShortEvent,
                        eventsExtraData = scheduleState.selectedSchedule!!.eventsExtraData,
                        events = eventsGrouped.second,
                        showEventDialog = showEventDialog
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    } else {
        Empty(
            title = "¯\\_(ツ)_/¯",
            subtitle = LocalContext.current.getString(R.string.empty_here)
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
    scheduleState: ScheduleState.Loaded,
): MutableList<Event> {
    val displayedEvents = mutableListOf<Event>()
    if (scheduleState.selectedSchedule!!.scheduleEntity.recurrence != null) {
        val weeksRemaining = abs(
            ChronoUnit.WEEKS.between(
                today,
                scheduleState.selectedSchedule.scheduleEntity.endDate
            )
        ).toInt().plus(1)
        for (week in 1..weeksRemaining) {
            val weekNumber =
                (week % scheduleState.selectedSchedule.scheduleEntity.recurrence!!.firstWeekNumber).plus(
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