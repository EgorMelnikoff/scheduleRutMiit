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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calculateCurrentWeek
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calculateFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeGreen
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeLightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeOrange
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemePink
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeRed
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeViolet
import com.egormelnikoff.schedulerutmiit.ui.theme.lightThemeYellow
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.ScheduleData
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
        calculateFirstDayOfWeek(
            scheduleEntity.startDate.plusWeeks(pagerWeeksState.currentPage.toLong())
        )
    }

    val displayMonth =
        if (firstDayOfCurrentWeek == calculateFirstDayOfWeek(selectedDate))
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
                    disabledContentColor = MaterialTheme.colorScheme.onSurface
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
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        onClick = {
                            scope.launch {
                                pagerWeeksState.animateScrollToPage(scheduleData.weeksStartIndex)
                            }
                            selectDate(scheduleData.defaultDate)
                        }
                    )
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = displayMonth.month.getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale.getDefault()
                    ).replaceFirstChar { it.uppercase() },
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (scheduleEntity.recurrence != null) {
                    val selectedWeek = calculateCurrentWeek(
                        date = firstDayOfCurrentWeek,
                        startDate = scheduleEntity.startDate,
                        firstPeriodNumber = scheduleEntity.recurrence.firstWeekNumber,
                        interval = scheduleEntity.recurrence.interval!!
                    )
                    val color = MaterialTheme.colorScheme.onSurface
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
                        textAlign = TextAlign.Center,
                        text = "${LocalContext.current.getString(R.string.week)} $selectedWeek",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
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
                    disabledContentColor = MaterialTheme.colorScheme.onSurface
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
                val firstDayOfWeek = calculateFirstDayOfWeek(
                    scheduleEntity.startDate.plusWeeks(index.toLong())
                )

                for (date in 0 until 7) {
                    val currentDate = firstDayOfWeek.plusDays(date.toLong())
                    val eventsByDay = if (scheduleData.periodicEventsForCalendar != null) {
                        val currentWeek = calculateCurrentWeek(
                            date = firstDayOfWeek,
                            startDate = scheduleEntity.startDate,
                            firstPeriodNumber = scheduleEntity.recurrence!!.firstWeekNumber,
                            interval = scheduleEntity.recurrence.interval!!
                        )
                        scheduleData.periodicEventsForCalendar[currentWeek]?.filter {
                            it.key == currentDate.dayOfWeek
                        }!!.values.flatten()
                    } else if (scheduleData.nonPeriodicEventsForCalendar != null) {
                        scheduleData.nonPeriodicEventsForCalendar.filter {
                            it.key == currentDate
                        }.values.flatten()
                    } else emptyList()

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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayOfWeek.take(2).lowercase(),
            fontSize = 12.sp,
            color = if (isDisabled) MaterialTheme.colorScheme.surface
            else if (currentDate.dayOfWeek.value == 7) {
                MaterialTheme.colorScheme.error
            } else MaterialTheme.colorScheme.onSurface
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else if (isToday) MaterialTheme.colorScheme.surface
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
                fontSize = 12.sp,
                color =
                    if (isDisabled) MaterialTheme.colorScheme.surface
                    else if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else if (currentDate.dayOfWeek.value == 7) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface
            )
        }
        if (isShowCountClasses) {
            Spacer(modifier = Modifier.height(8.dp))
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
                                1 -> lightThemeRed
                                2 -> lightThemeOrange
                                3 -> lightThemeYellow
                                4 -> lightThemeGreen
                                5 -> lightThemeLightBlue
                                6 -> lightThemeBlue
                                7 -> lightThemeViolet
                                8 -> lightThemePink
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
