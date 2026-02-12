package com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calendar

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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.extension.getCurrentWeek
import com.egormelnikoff.schedulerutmiit.app.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.theme.color.getColorByIndex
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HorizontalCalendar(
    isShowCountClasses: Boolean,
    namedScheduleData: NamedScheduleData,
    scheduleUiState: ScheduleUiState,
    scope: CoroutineScope
) {
    val scheduleEntity = namedScheduleData.scheduleData!!.scheduleEntity!!

    val firstDayOfCurrentWeek = remember(
        scheduleUiState.pagerWeeksState.currentPage,
        scheduleEntity
    ) {
        scheduleEntity.startDate
            .plusWeeks(scheduleUiState.pagerWeeksState.currentPage.toLong())
            .getFirstDayOfWeek()
    }

    val displayDate = if (firstDayOfCurrentWeek == scheduleUiState.selectedDate.getFirstDayOfWeek())
        scheduleUiState.selectedDate
    else firstDayOfCurrentWeek.plusDays(4L)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                enabled = scheduleUiState.pagerWeeksState.currentPage != 0,
                onClick = {
                    scope.launch {
                        scheduleUiState.pagerWeeksState.animateScrollToPage(scheduleUiState.pagerWeeksState.currentPage - 1)
                    }
                },
                colors = IconButtonColors(
                    containerColor = Color.Unspecified,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = Color.Unspecified,
                    disabledContentColor = MaterialTheme.colorScheme.secondaryContainer
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
                                scheduleUiState.pagerWeeksState.animateScrollToPage(
                                    namedScheduleData.scheduleData.schedulePagerData!!.weeksStartIndex
                                )
                            }
                            scheduleUiState.onSelectDate(namedScheduleData.scheduleData.schedulePagerData!!.defaultDate)
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
            ) {
                Text(
                    text = displayDate.month.getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale.getDefault()
                    ).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                namedScheduleData.scheduleData.scheduleEntity.recurrence?.let { recurrence ->
                    val selectedWeek = firstDayOfCurrentWeek.getCurrentWeek(
                        startDate = scheduleEntity.startDate,
                        recurrence = recurrence
                    )
                    val color = MaterialTheme.colorScheme.onSecondaryContainer
                    Icon(
                        modifier = Modifier.size(3.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.circle),
                        contentDescription = null,
                        tint = color
                    )
                    Text(
                        text = stringResource(
                            R.string.week,
                            selectedWeek.toString()
                        )
                            .replaceFirstChar { it.lowercase() },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }

            IconButton(
                enabled = scheduleUiState.pagerWeeksState.currentPage != namedScheduleData.scheduleData.schedulePagerData!!.weeksCount - 1,
                onClick = {
                    scope.launch {
                        scheduleUiState.pagerWeeksState.animateScrollToPage(scheduleUiState.pagerWeeksState.currentPage + 1)
                    }
                },
                colors = IconButtonColors(
                    containerColor = Color.Unspecified,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = Color.Unspecified,
                    disabledContentColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.right),
                    contentDescription = null,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = scheduleUiState.pagerWeeksState,
            verticalAlignment = Alignment.Top,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 12.dp
            )
        ) { index ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val firstDayOfWeek = scheduleEntity.startDate
                    .plusWeeks(index.toLong())
                    .getFirstDayOfWeek()

                stringArrayResource(R.array.days_of_week).forEachIndexed { index, day ->
                    val currentDate = firstDayOfWeek.plusDays(index.toLong())

                    val eventsForDate = currentDate.getEventsForDate(
                        scheduleEntity = scheduleEntity,
                        periodicEvents = namedScheduleData.scheduleData.periodicEvents,
                        nonPeriodicEvents = namedScheduleData.scheduleData.nonPeriodicEvents
                    )

                    HorizontalCalendarItem(
                        selectDate = scheduleUiState.onSelectDate,
                        dayOfWeek = day,
                        currentDate = currentDate,
                        events = eventsForDate,
                        eventsExtraData = namedScheduleData.scheduleData.eventsExtraData,
                        isShowCountClasses = isShowCountClasses,
                        isDisabled = currentDate !in scheduleEntity.startDate..scheduleEntity.endDate,
                        isSelected = currentDate == scheduleUiState.selectedDate,
                        isToday = (currentDate == namedScheduleData.scheduleData.schedulePagerData!!.today)
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
    dayOfWeek: String,
    isDisabled: Boolean,
    isSelected: Boolean,
    isShowCountClasses: Boolean,
    isToday: Boolean,
    events: Map<String, List<Event>>,
    eventsExtraData: List<EventExtraData>
) {
    Column(
        modifier = Modifier.width(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = dayOfWeek.lowercase(),
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
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isToday -> MaterialTheme.colorScheme.secondaryContainer
                        else -> Color.Unspecified
                    }
                )
                .let {
                    if (!isDisabled) {
                        it.clickable(
                            onClick = {
                                selectDate(currentDate)
                            }
                        )
                    } else {
                        it
                    }
                }
        ) {
            Text(
                text = currentDate.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isDisabled -> MaterialTheme.colorScheme.secondaryContainer
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    currentDate.dayOfWeek.value == 7 -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                }

            )
        }
        if (isShowCountClasses) {
            Box(
                modifier = Modifier.defaultMinSize(minHeight = 6.dp)
            ) {
                EventsSummary(
                    events = events,
                    eventsExtraData = eventsExtraData
                )
            }
        }
    }
}

@Composable
fun EventsSummary(
    events: Map<String, List<Event>>,
    eventsExtraData: List<EventExtraData>
) {
    FlowRow(
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
                        val eventExtraData = eventsExtraData.find { it.id == event.id }
                        val color = getColorByIndex(
                            index = eventExtraData?.tag,
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