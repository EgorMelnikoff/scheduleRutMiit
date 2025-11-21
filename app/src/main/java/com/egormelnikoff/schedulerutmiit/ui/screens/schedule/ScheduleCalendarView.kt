package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.getEventsForDate
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.EventView
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigateEventDialog
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState

@Composable
fun ScheduleCalendarView(
    navigateToEvent: (NavigateEventDialog) -> Unit,
    onDeleteEvent: (Pair<ScheduleEntity, Long>) -> Unit,
    onUpdateHiddenEvent: (Pair<ScheduleEntity, Long>) -> Unit,
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
            navigateToEvent = navigateToEvent,
            onDeleteEvent = onDeleteEvent,
            onUpdateHiddenEvent = onUpdateHiddenEvent,

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
    navigateToEvent: (NavigateEventDialog) -> Unit,
    onDeleteEvent: (Pair<ScheduleEntity, Long>) -> Unit,
    onUpdateHiddenEvent: (Pair<ScheduleEntity, Long>) -> Unit,

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

        val eventsForDate by remember (
            namedScheduleData.namedSchedule,
            namedScheduleData.scheduleData
        ){
            mutableStateOf(
                currentDate.getEventsForDate(
                    scheduleEntity = scheduleEntity,
                    periodicEvents = namedScheduleData.scheduleData.periodicEvents,
                    nonPeriodicEvents = namedScheduleData.scheduleData.nonPeriodicEvents
                ).toList()
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = paddingBottom
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (eventsForDate.isNotEmpty()) {
                items(
                    items = eventsForDate
                ) { events ->
                    Event(
                        navigateToEvent = navigateToEvent,
                        onDeleteEvent = onDeleteEvent,
                        onUpdateHiddenEvent = onUpdateHiddenEvent,

                        events = events.second,
                        scheduleEntity = scheduleEntity,
                        eventsExtraData = namedScheduleData.scheduleData.eventsExtraData,

                        isSavedSchedule = isSavedSchedule,
                        isCustomSchedule = namedScheduleData.namedSchedule?.namedScheduleEntity?.type == 3,
                        eventView = eventView
                    )
                }
            } else {
                item {
                    Empty(
                        modifier = Modifier.fillParentMaxSize(),
                        title = LocalContext.current.getString(R.string.day_off),
                        subtitle = LocalContext.current.getString(R.string.empty_day)
                    )
                }
            }
        }
    }
}