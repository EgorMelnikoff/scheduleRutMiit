package com.egormelnikoff.schedulerutmiit.ui.screens.schedule.split_weeks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButtonRow
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.EventsForDay
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.state.actions.EventActions
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData

@Composable
fun ScheduleSplitWeeksView(
    navigationActions: NavigationActions,
    eventActions: EventActions,
    appUiState: AppUiState,
    scheduleUiState: ScheduleUiState,
    namedScheduleData: NamedScheduleData,
    isSavedSchedule: Boolean,
    appSettings: AppSettings,
    paddingBottom: Dp
) {
    val scheduleEntity = namedScheduleData.scheduleData!!.scheduleEntity!!
    val weeks = (1..namedScheduleData.scheduleData.scheduleEntity.recurrence!!.interval).toList()

    Column {
        DaySelector(
            scheduleUiState = scheduleUiState,
            namedScheduleData = namedScheduleData,
            isShowCountClasses = appSettings.showCountClasses,
            selectedWeek = scheduleUiState.selectedWeek,
            scope = appUiState.scope
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
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
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = scheduleUiState.pagerSplitWeeks,
            verticalAlignment = Alignment.Top,
            pageSpacing = 12.dp
        ) { index ->
            val currentDate = scheduleEntity.startDate.plusDays(index.toLong())

            val eventsForDate by remember(
                namedScheduleData.namedSchedule,
                namedScheduleData.scheduleData,
                scheduleUiState.selectedWeek
            ) {
                mutableStateOf(
                    namedScheduleData.scheduleData.periodicEvents!!.getEventsByDayAndWeek(
                        currentDate.dayOfWeek,
                        scheduleUiState.selectedWeek
                    ).toList()
                )
            }

            EventsForDay(
                navigationActions = navigationActions,
                eventActions = eventActions,

                scheduleEntity = scheduleEntity,
                namedScheduleData = namedScheduleData,
                eventsForDate = eventsForDate,
                isSavedSchedule = isSavedSchedule,
                eventView = appSettings.eventView,
                paddingBottom = paddingBottom
            )
        }
    }
}