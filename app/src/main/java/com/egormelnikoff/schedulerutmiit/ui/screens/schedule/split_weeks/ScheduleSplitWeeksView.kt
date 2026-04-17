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
import com.egormelnikoff.schedulerutmiit.app.extension.getEventsByDayAndWeek
import com.egormelnikoff.schedulerutmiit.data.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButtonRow
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.EventsForDay
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto.NamedScheduleUiDto
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto.ScheduleUiDto

@Composable
fun ScheduleSplitWeeksView(
    scheduleViewModel: ScheduleViewModel,
    appUiState: AppUiState,
    scheduleUiState: ScheduleUiState,

    namedScheduleUiDto: NamedScheduleUiDto,
    scheduleUiDto: ScheduleUiDto,
    recurrence: RecurrenceDto,

    isSavedSchedule: Boolean,
    appSettings: AppSettings,
    paddingBottom: Dp
) {
    val weeks = (1..recurrence.interval).toList()

    Column {
        DaySelector(
            scheduleUiState = scheduleUiState,
            scheduleUiDto = scheduleUiDto,
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
            val currentDate = scheduleUiDto.scheduleEntity.startDate.plusDays(index.toLong())

            val eventsForDate by remember(
                namedScheduleUiDto.namedSchedule,
                namedScheduleUiDto.scheduleUiDto,
                scheduleUiState.selectedWeek
            ) {
                mutableStateOf(
                    scheduleUiDto.periodicEvents?.getEventsByDayAndWeek(
                        currentDate.dayOfWeek,
                        scheduleUiState.selectedWeek
                    )?.toList()
                )
            }

            EventsForDay(
                scheduleViewModel = scheduleViewModel,
                appBackStack = appUiState.appBackStack,

                namedScheduleEntity = namedScheduleUiDto.namedSchedule.namedScheduleEntity,
                scheduleEntity = scheduleUiDto.scheduleEntity,
                eventsForDate = eventsForDate,
                eventsExtraData = scheduleUiDto.eventsExtraData,
                isSavedSchedule = isSavedSchedule,
                eventView = appSettings.eventView,
                paddingBottom = paddingBottom
            )
        }
    }
}