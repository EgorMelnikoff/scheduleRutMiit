package com.egormelnikoff.schedulerutmiit.ui.screens.schedule.split_weeks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.app.extension.getEventsByDayAndWeek
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButtonRow
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.EventsForDay
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleData
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel

@Composable
fun ScheduleSplitWeeksView(
    scheduleViewModel: ScheduleViewModel,
    appUiState: AppUiState,
    scheduleUiState: ScheduleUiState,

    namedScheduleData: NamedScheduleData,
    scheduleData: ScheduleData,
    recurrence: Recurrence,

    isSavedSchedule: Boolean,
    appSettings: AppSettings,
    paddingBottom: Dp
) {
    val weeks = (1..recurrence.interval).toList()

    Column {
        DaySelector(
            scheduleUiState = scheduleUiState,
            scheduleData = scheduleData,
            eventsCountView = appSettings.eventsCountView,
            selectedWeek = scheduleUiState.selectedWeek,
            scope = appUiState.scope
        )
        if (weeks.size > 1) {
            Box(modifier = Modifier.padding(16.dp)) {
                CustomButtonRow(
                    selectedElement = scheduleUiState.selectedWeek,
                    elements = weeks,
                    onClick = { value ->
                        scheduleUiState.onSelectWeek(value)
                    }
                ) { week ->
                    Text(
                        text = stringResource(
                            R.string.week,
                            week.second
                        ),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = scheduleUiState.pagerSplitWeeks,
            verticalAlignment = Alignment.Top,
            pageSpacing = 12.dp
        ) { index ->
            val currentDate = scheduleData.scheduleEntity.startDate.plusDays(index.toLong())

            val eventsForDate by remember(
                namedScheduleData.namedSchedule,
                namedScheduleData.scheduleData,
                scheduleUiState.selectedWeek
            ) {
                mutableStateOf(
                    scheduleData.periodicEvents?.getEventsByDayAndWeek(
                        currentDate.dayOfWeek,
                        scheduleUiState.selectedWeek
                    )?.toList()
                )
            }

            EventsForDay(
                scheduleViewModel = scheduleViewModel,
                appBackStack = appUiState.appBackStack,

                namedScheduleEntity = namedScheduleData.namedSchedule.namedScheduleEntity,
                scheduleEntity = scheduleData.scheduleEntity,
                eventsForDate = eventsForDate,
                eventsExtraData = scheduleData.eventsExtraData,
                isSavedSchedule = isSavedSchedule,
                eventView = appSettings.eventView,
                paddingBottom = paddingBottom
            )
        }
    }
}