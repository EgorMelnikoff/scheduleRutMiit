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
    if (scheduleState.currentNamedScheduleData?.scheduleData?.scheduleEntity != null && scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData != null && scheduleUiState != null) {
        LaunchedEffect(
            scheduleState.currentNamedScheduleData.namedSchedule?.namedScheduleEntity?.apiId,
            scheduleState.currentNamedScheduleData.scheduleData.scheduleEntity.timetableId
        ) {
            scheduleUiState.onExpandSchedulesMenu(false)
            scheduleUiState.pagerDaysState.scrollToPage(
                scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.daysStartIndex
            )
            scheduleUiState.scheduleListState.scrollToItem(0)
        }

        LaunchedEffect(scheduleUiState.selectedDate) {
            val targetPage = ChronoUnit.DAYS.between(
                scheduleState.currentNamedScheduleData.scheduleData.scheduleEntity.startDate,
                scheduleUiState.selectedDate
            ).toInt()

            if (scheduleUiState.pagerDaysState.currentPage != targetPage) {
                scheduleUiState.pagerDaysState.scrollToPage(targetPage)
            }
        }

        LaunchedEffect(scheduleUiState.pagerDaysState.currentPage) {
            val newSelectedDate =
                scheduleState.currentNamedScheduleData.scheduleData.scheduleEntity.startDate.plusDays(
                    scheduleUiState.pagerDaysState.currentPage.toLong()
                )
            scheduleUiState.onSelectDate(newSelectedDate)

            val targetWeekIndex = ChronoUnit.WEEKS.between(
                scheduleState.currentNamedScheduleData.scheduleData.scheduleEntity.startDate.getFirstDayOfWeek(),
                newSelectedDate.getFirstDayOfWeek()
            ).toInt()

            if (scheduleUiState.pagerWeeksState.currentPage != targetWeekIndex) {
                scheduleUiState.pagerWeeksState.animateScrollToPage(targetWeekIndex)
            }
        }
    }
}