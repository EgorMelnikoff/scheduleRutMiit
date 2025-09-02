package com.egormelnikoff.schedulerutmiit.ui.schedule

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.composable.Empty
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@SuppressLint("FrequentlyChangingValue")
@Composable
fun ScheduleListView(
    onShowDialogEvent: (Pair<Event, EventExtraData?>) -> Unit,
    scheduleEntity: ScheduleEntity,
    eventsForList: List<Pair<LocalDate, List<Event>>>,
    eventsExtraData: List<EventExtraData>,
    scheduleListState: LazyListState,
    isShortEvent: Boolean,
    paddingBottom: Dp
) {
    if (eventsForList.isNotEmpty()) {
        val scope = rememberCoroutineScope()
        Box {
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
                            currentWeek = if (scheduleEntity.recurrence != null) {
                                calculateCurrentWeek(
                                    date = events.first,
                                    startDate = scheduleEntity.startDate,
                                    firstPeriodNumber = scheduleEntity.recurrence.firstWeekNumber,
                                    interval = scheduleEntity.recurrence.interval!!
                                )
                            } else null,
                            date = events.first,
                            formatter = formatter
                        )
                    }
                    val eventsForDayGrouped = events.second
                        .sortedBy { event -> event.startDatetime!!.toLocalTime() }
                        .groupBy { event ->
                            event.startDatetime.toString()
                        }
                        .toList()
                    items(eventsForDayGrouped) { eventsGrouped ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Event(
                                isShortEvent = isShortEvent,
                                eventsExtraData = eventsExtraData,
                                events = eventsGrouped.second,
                                onShowDialogEvent = onShowDialogEvent,
                            )
                        }
                        if (index != lastIndex) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = paddingBottom),
                visible = scheduleListState.firstVisibleItemIndex != 0,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        scope.launch {
                            scheduleListState.animateScrollToItem(0)
                        }
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.top),
                        contentDescription = null
                    )
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
    currentWeek: Int?,
    date: LocalDate,
    formatter: DateTimeFormatter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = color
        )
        if (currentWeek != null) {
            Canvas(
                modifier = Modifier
                    .size(3.dp)

            ) {
                drawCircle(
                    color = color,
                    center = center
                )
            }
            Text(
                text = "${LocalContext.current.getString(R.string.week)} $currentWeek",
                fontSize = 16.sp,
                color = color
            )
        }
    }
}