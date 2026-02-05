package com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.app.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.app.preferences.EventView
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.EventsForDay
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.state.actions.EventActions
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState

@Composable
fun ScheduleCalendarView(
    navigationActions: NavigationActions,
    eventActions: EventActions,
    appUiState: AppUiState,
    scheduleState: ScheduleState,
    scheduleUiState: ScheduleUiState,
    eventView: EventView,
    isShowCountClasses: Boolean,
    paddingBottom: Dp
) {
    Column {
        HorizontalCalendar(
            namedScheduleData = scheduleState.currentNamedScheduleData!!,
            scope = appUiState.scope,
            scheduleUiState = scheduleUiState,
            isShowCountClasses = isShowCountClasses
        )
        PagedDays(
            navigationActions = navigationActions,
            eventActions = eventActions,

            namedScheduleData = scheduleState.currentNamedScheduleData,
            pagerDaysState = scheduleUiState.pagerDaysState,

            isSavedSchedule = scheduleState.isSaved,
            eventView = eventView,
            paddingBottom = paddingBottom
        )
    }
}


@Composable
fun PagedDays(
    navigationActions: NavigationActions,
    eventActions: EventActions,

    namedScheduleData: NamedScheduleData,
    pagerDaysState: PagerState,

    isSavedSchedule: Boolean,
    eventView: EventView,
    paddingBottom: Dp
) {
    val scheduleEntity = namedScheduleData.scheduleData!!.scheduleEntity!!

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerDaysState,
        verticalAlignment = Alignment.Top,
        pageSpacing = 12.dp
    ) { index ->
        val currentDate = scheduleEntity.startDate.plusDays(index.toLong())

        val eventsForDate by remember(
            namedScheduleData.namedSchedule,
            namedScheduleData.scheduleData
        ) {
            mutableStateOf(
                currentDate.getEventsForDate(
                    scheduleEntity = scheduleEntity,
                    periodicEvents = namedScheduleData.scheduleData.periodicEvents,
                    nonPeriodicEvents = namedScheduleData.scheduleData.nonPeriodicEvents
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
            eventView = eventView,
            paddingBottom = paddingBottom

        )
    }
}