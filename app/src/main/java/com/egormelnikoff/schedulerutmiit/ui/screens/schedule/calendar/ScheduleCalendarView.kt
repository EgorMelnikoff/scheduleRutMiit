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
import com.egormelnikoff.schedulerutmiit.app.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.app.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.app.preferences.EventView
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.EventsForDay
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ui_dto.NamedScheduleUiDto
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ui_dto.ScheduleUiDto

@Composable
fun ScheduleCalendarView(
    scheduleViewModel: ScheduleViewModel,
    appUiState: AppUiState,
    namedScheduleUiDto: NamedScheduleUiDto,
    scheduleUiDto: ScheduleUiDto,
    isSavedSchedule: Boolean,
    scheduleUiState: ScheduleUiState,
    eventView: EventView,
    eventsCountView: EventsCountView,
    paddingBottom: Dp
) {
    Column {
        HorizontalCalendar(
            scheduleUiDto = scheduleUiDto,
            scope = appUiState.scope,
            scheduleUiState = scheduleUiState,
            eventsCountView = eventsCountView
        )
        PagedDays(
            scheduleViewModel = scheduleViewModel,
            appBackStack = appUiState.appBackStack,

            namedScheduleUiDto = namedScheduleUiDto,
            scheduleUiDto = scheduleUiDto,
            pagerDaysState = scheduleUiState.pagerDaysState,

            isSavedSchedule = isSavedSchedule,
            eventView = eventView,
            paddingBottom = paddingBottom
        )
    }
}


@Composable
fun PagedDays(
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,

    namedScheduleUiDto: NamedScheduleUiDto,
    scheduleUiDto: ScheduleUiDto,
    pagerDaysState: PagerState,

    isSavedSchedule: Boolean,
    eventView: EventView,
    paddingBottom: Dp
) {
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerDaysState,
        verticalAlignment = Alignment.Top,
        pageSpacing = 12.dp
    ) { index ->
        val currentDate = scheduleUiDto.scheduleEntity.startDate.plusDays(index.toLong())

        val eventsForDate by remember(
            namedScheduleUiDto.namedSchedule,
            scheduleUiDto
        ) {
            mutableStateOf(
                currentDate.getEventsForDate(
                    scheduleEntity = scheduleUiDto.scheduleEntity,
                    periodicEvents = scheduleUiDto.periodicEvents,
                    nonPeriodicEvents = scheduleUiDto.nonPeriodicEvents
                ).toList()
            )
        }

        EventsForDay(
            scheduleViewModel = scheduleViewModel,
            appBackStack = appBackStack,

            namedScheduleEntity = namedScheduleUiDto.namedSchedule.namedScheduleEntity,
            scheduleEntity = scheduleUiDto.scheduleEntity,
            eventsExtraData = scheduleUiDto.eventsExtraData,
            eventsForDate = eventsForDate,
            isSavedSchedule = isSavedSchedule,
            eventView = eventView,
            paddingBottom = paddingBottom

        )
    }
}