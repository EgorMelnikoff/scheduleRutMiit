package com.egormelnikoff.schedulerutmiit.ui.screens.schedule.split_weeks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.extension.getEventsByDayAndWeek
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calendar.EventsSummary
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DaySelector(
    scheduleUiState: ScheduleUiState,
    namedScheduleData: NamedScheduleData,
    selectedWeek: Int,
    isShowCountClasses: Boolean,
    scope: CoroutineScope
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val firstDayOfWeek =
            namedScheduleData.scheduleData!!.scheduleEntity!!.startDate.getFirstDayOfWeek()

        stringArrayResource(R.array.days_of_week).forEachIndexed { index, day ->
            val currentDate = firstDayOfWeek.plusDays(index.toLong())

            val eventsForDate =
                namedScheduleData.scheduleData.periodicEvents!!.getEventsByDayAndWeek(
                    dayOfWeek = currentDate.dayOfWeek,
                    week = selectedWeek
                )

            DaySelectorItem(
                title = day,
                events = eventsForDate,
                eventsExtraData = namedScheduleData.scheduleData.eventsExtraData,
                isShowCountClasses = isShowCountClasses,
                isSelected = index == scheduleUiState.pagerSplitWeeks.currentPage,
                isSunday = currentDate.dayOfWeek.value == 7,
                currentPage = index,
                selectPage = { date ->
                    scope.launch {
                        scheduleUiState.pagerSplitWeeks.scrollToPage(date)
                    }
                },
                isToday = (currentDate == namedScheduleData.scheduleData.schedulePagerData!!.today)
            )
        }
    }
}

@Composable
fun DaySelectorItem(
    selectPage: (Int) -> Unit,
    currentPage: Int,
    title: String,
    isSunday: Boolean,
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
                .clickable(
                    onClick = {
                        selectPage(currentPage)
                    }
                )

        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isSunday -> MaterialTheme.colorScheme.error
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
