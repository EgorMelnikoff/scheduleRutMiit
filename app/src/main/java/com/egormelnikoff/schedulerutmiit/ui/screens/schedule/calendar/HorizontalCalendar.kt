package com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.app.extension.getCurrentWeek
import com.egormelnikoff.schedulerutmiit.app.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.theme.color.getColorByIndex
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto.ScheduleUiDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HorizontalCalendar(
    eventsCountView: EventsCountView,
    scheduleUiDto: ScheduleUiDto,
    scheduleUiState: ScheduleUiState,
    scope: CoroutineScope
) {
    val firstDayOfCurrentWeek = remember(
        scheduleUiState.pagerWeeksState.currentPage,
        scheduleUiDto.scheduleEntity
    ) {
        scheduleUiDto.scheduleEntity.startDate
            .plusWeeks(scheduleUiState.pagerWeeksState.currentPage.toLong())
            .getFirstDayOfWeek()
    }

    val displayDate =
        if (firstDayOfCurrentWeek == scheduleUiState.selectedDate.getFirstDayOfWeek()) {
            scheduleUiState.selectedDate
        } else firstDayOfCurrentWeek.plusDays(3L)


    val enabledLeftButton by remember {
        derivedStateOf {
            scheduleUiState.pagerWeeksState.currentPage != 0
        }
    }
    val enabledRightButton by remember {
        derivedStateOf {
            scheduleUiState.pagerWeeksState.currentPage != scheduleUiDto.schedulePagerUiDto.weeksCount - 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .combinedClickable(
                        onClick = {
                            scope.launch {
                                scheduleUiState.pagerWeeksState.animateScrollToPage(scheduleUiState.pagerWeeksState.currentPage - 1)
                            }
                        },
                        onLongClick = {
                            scope.launch {
                                scheduleUiState.pagerWeeksState.animateScrollToPage(0)
                            }
                        }
                    )
                    .padding(8.dp),
                imageVector = ImageVector.vectorResource(R.drawable.left),
                tint = if (enabledLeftButton)
                    MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.secondaryContainer,
                contentDescription = null
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            scope.launch {
                                scheduleUiState.pagerWeeksState.animateScrollToPage(
                                    scheduleUiDto.schedulePagerUiDto.weeksStartIndex
                                )
                            }
                            scheduleUiState.onSelectDate(scheduleUiDto.schedulePagerUiDto.defaultDate)
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .height(32.dp),
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
                if (scheduleUiDto.scheduleEntity.recurrence != null && scheduleUiDto.scheduleEntity.recurrence.interval > 1) {
                    val selectedWeek = firstDayOfCurrentWeek.getCurrentWeek(
                        startDate = scheduleUiDto.scheduleEntity.startDate,
                        recurrence = scheduleUiDto.scheduleEntity.recurrence
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
                        ).replaceFirstChar { it.lowercase() },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (scheduleUiState.pagerWeeksState.currentPage + 1).toString(),
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .combinedClickable(
                        onClick = {
                            scope.launch {
                                scheduleUiState.pagerWeeksState.animateScrollToPage(scheduleUiState.pagerWeeksState.currentPage + 1)
                            }
                        },
                        onLongClick = {
                            scope.launch {
                                scheduleUiState.pagerWeeksState.animateScrollToPage(
                                    scheduleUiDto.schedulePagerUiDto.weeksCount - 1
                                )
                            }
                        }
                    )
                    .padding(8.dp),
                imageVector = ImageVector.vectorResource(R.drawable.right),
                tint = if (enabledRightButton)
                    MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.secondaryContainer,
                contentDescription = null
            )
        }
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = scheduleUiState.pagerWeeksState,
            verticalAlignment = Alignment.Top,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(
                top = 8.dp, bottom = 12.dp,
                start = 16.dp, end = 16.dp
            )
        ) { index ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val firstDayOfWeek = scheduleUiDto.scheduleEntity.startDate
                    .plusWeeks(index.toLong())
                    .getFirstDayOfWeek()

                stringArrayResource(R.array.days_of_week).forEachIndexed { index, day ->
                    val currentDate = firstDayOfWeek.plusDays(index.toLong())

                    val eventsForDate = currentDate.getEventsForDate(
                        scheduleEntity = scheduleUiDto.scheduleEntity,
                        periodicEvents = scheduleUiDto.periodicEvents,
                        nonPeriodicEvents = scheduleUiDto.nonPeriodicEvents
                    )

                    HorizontalCalendarItem(
                        selectDate = scheduleUiState.onSelectDate,
                        dayOfWeek = day,
                        currentDate = currentDate,
                        events = eventsForDate,
                        eventsExtraData = scheduleUiDto.eventsExtraData,
                        eventsCountView = eventsCountView,
                        isDisabled = currentDate !in scheduleUiDto.scheduleEntity.startDate..scheduleUiDto.scheduleEntity.endDate,
                        isSelected = currentDate == scheduleUiState.selectedDate,
                        isToday = (currentDate == scheduleUiDto.schedulePagerUiDto.today)
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
    eventsCountView: EventsCountView,
    isToday: Boolean,
    events: Map<String, List<Event>>,
    eventsExtraData: List<EventExtraData>
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.secondaryContainer
        else -> Color.Unspecified
    }
    val textColor = when {
        isDisabled -> MaterialTheme.colorScheme.secondaryContainer
        isSelected -> MaterialTheme.colorScheme.onPrimary
        (currentDate.dayOfWeek.value == 7) -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Column(
        modifier = Modifier.defaultMinSize(
            minWidth = 40.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    color = backgroundColor
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
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = dayOfWeek.lowercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            Text(
                text = currentDate.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
        if (eventsCountView == EventsCountView.DETAILS) {
            EventsDetailSummary(
                events = events,
                eventsExtraData = eventsExtraData
            )
        } else if (eventsCountView == EventsCountView.BRIEFLY && events.isNotEmpty()) {
            EventsBrieflySummary(
                eventsSize = events.size
            )
        }
    }
}

@Composable
fun EventsDetailSummary(
    events: Map<String, List<Event>>,
    eventsExtraData: List<EventExtraData>
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
                        val eventExtraData = eventsExtraData.find { it.id == event.id }
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

@Composable
fun EventsBrieflySummary(
    eventsSize: Int
) {
    Badge(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = eventsSize.toString()
        )
    }
}