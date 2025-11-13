package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.egormelnikoff.schedulerutmiit.app.model.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState
import java.time.temporal.ChronoUnit

@Composable
fun ScheduleUiStateSynchronizer(
    scheduleUiState: ScheduleUiState?,
    scheduleState: ScheduleState,
) {
    if (scheduleState.currentNamedScheduleData?.settledScheduleEntity != null && scheduleState.currentNamedScheduleData.schedulePagerData != null && scheduleUiState != null) {
        LaunchedEffect(
            scheduleState.currentNamedScheduleData.namedSchedule!!.namedScheduleEntity.apiId,
            scheduleState.currentNamedScheduleData.settledScheduleEntity.timetableId
        ) {
            scheduleUiState.onExpandSchedulesMenu(false)
            scheduleUiState.pagerDaysState.scrollToPage(
                scheduleState.currentNamedScheduleData.schedulePagerData.daysStartIndex
            )
            scheduleUiState.scheduleListState.scrollToItem(0)
        }
        LaunchedEffect(scheduleUiState.selectedDate) {
            val targetPage = ChronoUnit.DAYS.between(
                scheduleState.currentNamedScheduleData.settledScheduleEntity.startDate,
                scheduleUiState.selectedDate
            ).toInt()

            if (scheduleUiState.pagerDaysState.currentPage != targetPage) {
                scheduleUiState.pagerDaysState.scrollToPage(targetPage)
            }
        }

        LaunchedEffect(scheduleUiState.pagerDaysState.currentPage) {
            val newSelectedDate =
                scheduleState.currentNamedScheduleData.settledScheduleEntity.startDate.plusDays(
                    scheduleUiState.pagerDaysState.currentPage.toLong()
                )
            scheduleUiState.onSelectDate(newSelectedDate)

            val targetWeekIndex = ChronoUnit.WEEKS.between(
                scheduleState.currentNamedScheduleData.settledScheduleEntity.startDate.getFirstDayOfWeek(),
                newSelectedDate.getFirstDayOfWeek()
            ).toInt()

            if (scheduleUiState.pagerWeeksState.currentPage != targetWeekIndex) {
                scheduleUiState.pagerWeeksState.animateScrollToPage(targetWeekIndex)
            }
        }
    }
}