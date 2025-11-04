package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.getGroupedEvents
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduleListView(
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onUpdateHiddenEvent: (Long) -> Unit,

    scheduleUiState: ScheduleUiState,
    scheduleState: ScheduleState,
    isShortEvent: Boolean,
    paddingBottom: Dp
) {
    val scheduleData = scheduleUiState.currentNamedScheduleData!!
    val formatter = DateTimeFormatter.ofPattern("d MMMM")

    if (scheduleData.eventForList.isNotEmpty()) {
        LazyColumn(
            state = scheduleState.scheduleListState,
            contentPadding = PaddingValues(bottom = paddingBottom),
            modifier = Modifier.fillMaxSize(),
        ) {
            scheduleData.eventForList.forEachIndexed { index, events ->
                val eventsForDay = events.second.getGroupedEvents().toList()
                stickyHeader {
                    DateHeader(
                        date = events.first,
                        formatter = formatter
                    )
                }

                items(eventsForDay) { eventsGrouped ->
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Event(
                            navigateToEvent = navigateToEvent,
                            onDeleteEvent = onDeleteEvent,
                            onUpdateHiddenEvent = onUpdateHiddenEvent,
                            events = eventsGrouped.second,
                            eventsExtraData = scheduleData.eventsExtraData,
                            isSavedSchedule = scheduleUiState.isSaved,
                            isShortEvent = isShortEvent
                        )
                    }
                    if (index != scheduleData.eventForList.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    } else {
        Empty(
            title = "¯\\_(ツ)_/¯",
            subtitle = LocalContext.current.getString(R.string.no_classes),
            isBoldTitle = false,
            paddingBottom = paddingBottom
        )
    }
}

@Composable
fun DateHeader(
    date: LocalDate,
    formatter: DateTimeFormatter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val color = MaterialTheme.colorScheme.primary
        Icon(
            modifier = Modifier.width(16.dp),
            imageVector = ImageVector.vectorResource(R.drawable.calendar),
            contentDescription = null,
            tint = color
        )
        Text(
            text = "${
                date.dayOfWeek.getDisplayName(
                    TextStyle.SHORT_STANDALONE,
                    Locale.getDefault()
                ).replaceFirstChar { it.uppercase() }
            }, ${formatter.format(date)}",
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}