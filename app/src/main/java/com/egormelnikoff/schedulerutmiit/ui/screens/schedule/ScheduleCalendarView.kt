package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.elements.HorizontalCalendar
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleUiState
import java.time.DayOfWeek
import java.time.LocalDate

data class ScheduleCalendarState(
    val pagerDaysState: PagerState,
    val pagerWeeksState: PagerState,
    val selectedDate: LocalDate,
    val selectDate: (LocalDate) -> Unit
)

@Composable
fun ScheduleCalendarView(
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onUpdateHiddenEvent: (Long) -> Unit,
    scheduleUiState: ScheduleUiState,
    scheduleCalendarState: ScheduleCalendarState,
    today: LocalDate,
    isSavedSchedule: Boolean,
    isShortEvent: Boolean,
    isShowCountClasses: Boolean,
    paddingBottom: Dp
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalCalendar(
            scheduleEntity = scheduleUiState.currentScheduleData!!.settledScheduleEntity!!,
            eventsExtraData = scheduleUiState.currentScheduleData.eventsExtraData,

            isShowCountClasses = isShowCountClasses,
            today = today,
            scheduleData = scheduleUiState.currentScheduleData,

            pagerWeeksState = scheduleCalendarState.pagerWeeksState,
            selectedDate = scheduleCalendarState.selectedDate,

            selectDate = scheduleCalendarState.selectDate
        )
        PagedDays(
            navigateToEvent = navigateToEvent,
            onDeleteEvent = onDeleteEvent,
            onUpdateHiddenEvent = onUpdateHiddenEvent,
            scheduleEntity = scheduleUiState.currentScheduleData.settledScheduleEntity,
            periodicEvents = scheduleUiState.currentScheduleData.periodicEventsForCalendar,
            nonPeriodicEvents = scheduleUiState.currentScheduleData.nonPeriodicEventsForCalendar,
            eventsExtraData = scheduleUiState.currentScheduleData.eventsExtraData,

            pagerDaysState = scheduleCalendarState.pagerDaysState,
            isSavedSchedule = isSavedSchedule,
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
    scheduleEntity: ScheduleEntity,
    periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
    nonPeriodicEvents: Map<LocalDate, List<Event>>?,
    eventsExtraData: List<EventExtraData>,

    pagerDaysState: PagerState,
    isSavedSchedule: Boolean,
    isShortEvent: Boolean,
    paddingBottom: Dp
) {
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerDaysState,
        verticalAlignment = Alignment.Top,
        pageSpacing = 12.dp
    ) { index ->
        val currentDate = scheduleEntity.startDate.plusDays(index.toLong())

        val eventsForDay = if (periodicEvents != null) {
            val currentWeek = calculateCurrentWeek(
                date = currentDate,
                startDate = scheduleEntity.startDate,
                firstPeriodNumber = scheduleEntity.recurrence!!.firstWeekNumber,
                interval = scheduleEntity.recurrence.interval!!
            )
            periodicEvents[currentWeek]?.filter {
                it.key == currentDate.dayOfWeek
            }!!.values.flatten()
        } else nonPeriodicEvents?.filter {
            it.key == currentDate
        }?.values?.flatten()
            ?: emptyList()

        val eventsForDayGrouped = eventsForDay
            .filter { !it.isHidden }
            .sortedBy { event -> event.startDatetime!!.toLocalTime() }
            .groupBy { event ->
                Pair(
                    event.startDatetime!!.toLocalTime(),
                    event.endDatetime!!.toLocalTime()
                )
            }
            .toList()

        if (eventsForDayGrouped.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = paddingBottom
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                eventsForDayGrouped.forEach { events ->
                    ScheduleEvent(
                        navigateToEvent = navigateToEvent,
                        onDeleteEvent = onDeleteEvent,
                        onUpdateHiddenEvent = onUpdateHiddenEvent,
                        events = events.second,
                        isSavedSchedule = isSavedSchedule,
                        isShortEvent = isShortEvent,
                        eventsExtraData = eventsExtraData
                    )
                }
            }
        } else {
            Empty(
                title = LocalContext.current.getString(R.string.day_off),
                subtitle = LocalContext.current.getString(R.string.empty_day),
                paddingBottom = paddingBottom
            )
        }
    }
}