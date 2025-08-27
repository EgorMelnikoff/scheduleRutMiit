package com.egormelnikoff.schedulerutmiit.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.ui.composable.Empty
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduleListView(
    onShowDialogEvent: (Pair<Event, EventExtraData?>) -> Unit,
    scheduleListState: LazyListState,
    isShortEvent: Boolean,
    eventsForList: List<Pair<LocalDate, List<Event>>>,
    eventsExtraData: List<EventExtraData>,
    paddingBottom: Dp
) {
    if (eventsForList.isNotEmpty()) {
        val scope = rememberCoroutineScope()
        LazyColumn(
            state = scheduleListState,
            contentPadding = PaddingValues(bottom = paddingBottom),
            modifier = Modifier.fillMaxSize(),
        ) {
            val lastIndex = eventsForList.lastIndex
            val formatter = DateTimeFormatter.ofPattern("d MMMM")
            eventsForList.forEachIndexed { index, events ->
                stickyHeader {
                    DateHeader(
                        date = events.first,
                        formatter = formatter,
                        onClick = {
                            scope.launch {
                                scheduleListState.animateScrollToItem(0)
                            }
                        }
                    )
                }
                val eventsForDayGrouped = events.second
                    .sortedBy { event -> event.startDatetime!!.toLocalTime() }
                    .groupBy { event ->
                        event.startDatetime.toString()
                    }
                    .toList()
                items(eventsForDayGrouped) { eventsGrouped ->
                    Box (
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ){
                        Event(
                            isShortEvent = isShortEvent,
                            eventsExtraData = eventsExtraData,
                            events = eventsGrouped.second,
                            onShowDialogEvent = onShowDialogEvent,
                        )
                    }
                    if (index != lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
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
    formatter: DateTimeFormatter,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp),
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