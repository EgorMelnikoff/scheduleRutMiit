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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.getEventsForDate
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleUiState

@Composable
fun ScheduleCalendarView(
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onUpdateHiddenEvent: (Long) -> Unit,
    scheduleUiState: ScheduleUiState,
    scheduleState: ScheduleState,
    isShortEvent: Boolean,
    isShowCountClasses: Boolean,
    paddingBottom: Dp
) {
    Column {
        HorizontalCalendar(
            scheduleData = scheduleUiState.currentNamedScheduleData!!,
            scheduleState = scheduleState,
            isShowCountClasses = isShowCountClasses
        )
        PagedDays(
            navigateToEvent = navigateToEvent,
            onDeleteEvent = onDeleteEvent,
            onUpdateHiddenEvent = onUpdateHiddenEvent,

            scheduleData = scheduleUiState.currentNamedScheduleData,
            pagerDaysState = scheduleState.pagerDaysState,

            isSavedSchedule = scheduleUiState.isSaved,
            isShortEvent = isShortEvent,
            paddingBottom = paddingBottom
        )
    }
}


@Composable
fun PagedDays(
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onUpdateHiddenEvent: (Long) -> Unit,

    scheduleData: NamedScheduleData,
    pagerDaysState: PagerState,

    isSavedSchedule: Boolean,
    isShortEvent: Boolean,
    paddingBottom: Dp
) {
    val scheduleEntity = scheduleData.settledScheduleEntity!!

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerDaysState,
        verticalAlignment = Alignment.Top,
        pageSpacing = 12.dp
    ) { index ->
        val currentDate = scheduleEntity.startDate.plusDays(index.toLong())

        val eventsForDate = currentDate.getEventsForDate(
            scheduleEntity = scheduleEntity,
            periodicEvents = scheduleData.periodicEvents,
            nonPeriodicEvents = scheduleData.nonPeriodicEvents
        ).toList()

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = paddingBottom
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (eventsForDate.isNotEmpty()) {
                items(eventsForDate) { events ->
                    Event(
                        navigateToEvent = navigateToEvent,
                        onDeleteEvent = onDeleteEvent,
                        onUpdateHiddenEvent = onUpdateHiddenEvent,

                        events = events.second,
                        eventsExtraData = scheduleData.eventsExtraData,

                        isSavedSchedule = isSavedSchedule,
                        isShortEvent = isShortEvent
                    )
                }
            } else {
                item {
                    Empty(
                        modifier = Modifier.fillParentMaxSize(),
                        title = LocalContext.current.getString(R.string.day_off),
                        subtitle = LocalContext.current.getString(R.string.empty_day),
                        paddingBottom = paddingBottom
                    )
                }
            }
        }
    }
}