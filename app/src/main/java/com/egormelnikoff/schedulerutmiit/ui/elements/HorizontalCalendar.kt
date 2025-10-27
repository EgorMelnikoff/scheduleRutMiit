package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.calculateCurrentWeek
import com.egormelnikoff.schedulerutmiit.app.model.calculateFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.theme.Blue
import com.egormelnikoff.schedulerutmiit.ui.theme.Green
import com.egormelnikoff.schedulerutmiit.ui.theme.LightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.Orange
import com.egormelnikoff.schedulerutmiit.ui.theme.Pink
import com.egormelnikoff.schedulerutmiit.ui.theme.Red
import com.egormelnikoff.schedulerutmiit.ui.theme.Violet
import com.egormelnikoff.schedulerutmiit.ui.theme.Yellow
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleData
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HorizontalCalendar(
    isShowCountClasses: Boolean,
    today: LocalDate,
    scheduleData: ScheduleData,
    scheduleEntity: ScheduleEntity,
    eventsExtraData: List<EventExtraData>,
    pagerWeeksState: PagerState,
    selectedDate: LocalDate,
    selectDate: (LocalDate) -> Unit
) {
    val scope = rememberCoroutineScope()
    val firstDayOfCurrentWeek = remember(
        pagerWeeksState.currentPage,
        scheduleEntity
    ) {
        scheduleEntity.startDate
            .plusWeeks(pagerWeeksState.currentPage.toLong())
            .calculateFirstDayOfWeek()
    }

    val displayMonth =
        if (firstDayOfCurrentWeek == selectedDate.calculateFirstDayOfWeek())
            selectedDate
        else
            firstDayOfCurrentWeek.plusDays(4L)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                enabled = pagerWeeksState.currentPage != 0,
                onClick = {
                    scope.launch {
                        pagerWeeksState.animateScrollToPage(pagerWeeksState.currentPage - 1)
                    }
                },
                colors = IconButtonColors(
                    containerColor = Color.Unspecified,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = Color.Unspecified,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.left),
                    contentDescription = null
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.small)
                    .clickable(
                        onClick = {
                            scope.launch {
                                pagerWeeksState.animateScrollToPage(scheduleData.weeksStartIndex)
                            }
                            selectDate(scheduleData.defaultDate)
                        }
                    )
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
            ) {
                Text(
                    text = displayMonth.month.getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale.getDefault()
                    ).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                scheduleEntity.recurrence?.let { recurrence ->
                    val selectedWeek = calculateCurrentWeek(
                        date = firstDayOfCurrentWeek,
                        startDate = scheduleEntity.startDate,
                        firstPeriodNumber = recurrence.firstWeekNumber,
                        interval = recurrence.interval!!
                    )
                    val color = MaterialTheme.colorScheme.onSecondaryContainer
                    Icon(
                        modifier = Modifier.size(3.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.circle),
                        contentDescription = null,
                        tint = color
                    )
                    Text(
                        text = "${
                            LocalContext.current.getString(R.string.week)
                                .replaceFirstChar { it.lowercase() }
                        } $selectedWeek",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }

            IconButton(
                enabled = pagerWeeksState.currentPage != scheduleData.weeksCount - 1,
                onClick = {
                    scope.launch {
                        pagerWeeksState.animateScrollToPage(pagerWeeksState.currentPage + 1)
                    }
                },
                colors = IconButtonColors(
                    containerColor = Color.Unspecified,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = Color.Unspecified,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.right),
                    contentDescription = null,
                )
            }
        }
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerWeeksState,
            verticalAlignment = Alignment.Top,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 12.dp
            )
        ) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val firstDayOfWeek = scheduleEntity.startDate
                    .plusWeeks(index.toLong())
                    .calculateFirstDayOfWeek()

                for (date in 0 until 7) {
                    val currentDate = firstDayOfWeek.plusDays(date.toLong())
                    val eventsByDay = scheduleData.periodicEventsForCalendar?.let { events ->
                        val currentWeek = calculateCurrentWeek(
                            date = firstDayOfWeek,
                            startDate = scheduleEntity.startDate,
                            firstPeriodNumber = scheduleEntity.recurrence!!.firstWeekNumber,
                            interval = scheduleEntity.recurrence.interval!!
                        )
                        events[currentWeek]?.filter {
                            it.key == currentDate.dayOfWeek
                        }!!.values.flatten()
                    } ?: scheduleData.nonPeriodicEventsForCalendar?.filter {
                        it.key == currentDate
                    }?.values?.flatten() ?: emptyList()

                    HorizontalCalendarItem(
                        selectDate = selectDate,
                        currentDate = currentDate,
                        events = eventsByDay,
                        eventsExtraData = eventsExtraData,

                        isShowCountClasses = isShowCountClasses,
                        isDisabled = currentDate !in scheduleEntity.startDate..scheduleEntity.endDate,
                        isSelected = currentDate == selectedDate,
                        isToday = (currentDate == today)
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalCalendarItem(
    selectDate: (LocalDate) -> Unit,
    currentDate: LocalDate,
    isDisabled: Boolean,
    isSelected: Boolean,
    isShowCountClasses: Boolean,
    isToday: Boolean,
    events: List<Event>,
    eventsExtraData: List<EventExtraData>
) {
    val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

    Column(
        modifier = Modifier.width(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = dayOfWeek.take(2).lowercase(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDisabled) MaterialTheme.colorScheme.secondaryContainer
            else if (currentDate.dayOfWeek.value == 7) {
                MaterialTheme.colorScheme.error
            } else MaterialTheme.colorScheme.onSecondaryContainer
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else if (isToday) MaterialTheme.colorScheme.secondaryContainer
                    else Color.Unspecified
                )
                .let {
                    if (!isDisabled) {
                        it.clickable(onClick = {
                            selectDate(currentDate)
                        })
                    } else {
                        it
                    }
                }
        ) {
            Text(
                text = currentDate.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color =
                    if (isDisabled) MaterialTheme.colorScheme.secondaryContainer
                    else if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else if (currentDate.dayOfWeek.value == 7) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        if (isShowCountClasses) {
            val eventsByStartTime = events
                .sortedBy { it.startDatetime!!.toLocalTime() }
                .groupBy { event ->
                    Pair(
                        event.startDatetime!!.toLocalTime(),
                        event.endDatetime!!.toLocalTime()
                    )
                }
            FlowRow(
                maxItemsInEachRow = if (events.size in 6..8) 4 else 5,
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (groupedEvents in eventsByStartTime) {
                    var offset = 0
                    Box {
                        for (event in groupedEvents.value) {
                            val eventExtraData = eventsExtraData.find {
                                it.id == event.id
                            }
                            val color = when (eventExtraData?.tag) {
                                1 -> Red
                                2 -> Orange
                                3 -> Yellow
                                4 -> Green
                                5 -> LightBlue
                                6 -> Blue
                                7 -> Violet
                                8 -> Pink
                                else -> MaterialTheme.colorScheme.onBackground
                            }
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
}
