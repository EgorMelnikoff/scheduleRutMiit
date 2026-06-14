package com.egormelnikoff.schedulerutmiit.schedule.ui.screen

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ScheduleState
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun ScheduleUiStateSynchronizer(
    scheduleCalendarState: CalendarState?,
    scheduleListState: LazyListState,
    scheduleState: ScheduleState,
    namedScheduleState: NamedScheduleState,
    hourlyDateTime: LocalDateTime,
    scheduleViewModel: ScheduleViewModel,
) {
    if (scheduleState.scheduleUiDto?.schedule != null && scheduleCalendarState != null) {
        LaunchedEffect(
            namedScheduleState.namedScheduleWithSchedules?.namedSchedule?.apiId,
            scheduleState.scheduleUiDto.schedule.timetableId
        ) {
            scheduleCalendarState.pagerDaysState.scrollToPage(
                scheduleState.scheduleUiDto.calendarData.daysPagerDefaultIndex
            )
            scheduleListState.scrollToItem(0)
        }

        LaunchedEffect(scheduleCalendarState.selectedDate) {
            val targetPage = ChronoUnit.DAYS.between(
                scheduleState.scheduleUiDto.schedule.startDate,
                scheduleCalendarState.selectedDate
            ).toInt()

            if (scheduleCalendarState.pagerDaysState.currentPage != targetPage) {
                scheduleCalendarState.pagerDaysState.scrollToPage(targetPage)
            }
        }

        LaunchedEffect(scheduleCalendarState.pagerDaysState.currentPage) {
            val newSelectedDate =
                scheduleState.scheduleUiDto.schedule.startDate.plusDays(
                    scheduleCalendarState.pagerDaysState.currentPage.toLong()
                )
            scheduleCalendarState.onSelectDate(newSelectedDate)

            val targetWeekIndex = ChronoUnit.WEEKS.between(
                scheduleState.scheduleUiDto.schedule.startDate.getFirstDayOfWeek(),
                newSelectedDate.getFirstDayOfWeek()
            ).toInt()

            if (scheduleCalendarState.pagerWeeksState.currentPage != targetWeekIndex) {
                scheduleCalendarState.pagerWeeksState.animateScrollToPage(targetWeekIndex)
            }
        }

        LaunchedEffect(hourlyDateTime) {
            scheduleViewModel.refreshReview()
        }
    }
}
