package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.egormelnikoff.schedulerutmiit.app.extension.getCurrentWeek
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ScheduleState
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun ScheduleUiStateSynchronizer(
    scheduleUiState: ScheduleUiState?,
    scheduleState: ScheduleState,
    currentDateTime: LocalDateTime,
    scheduleViewModel: ScheduleViewModel,
) {
    if (scheduleState.currentNamedSchedule?.scheduleUiDto?.scheduleEntity != null && scheduleUiState != null) {
        LaunchedEffect(
            scheduleState.currentNamedSchedule.namedSchedule.namedScheduleEntity.apiId,
            scheduleState.currentNamedSchedule.scheduleUiDto.scheduleEntity.timetableId
        ) {
            scheduleUiState.pagerDaysState.scrollToPage(
                scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.daysStartIndex
            )
            scheduleUiState.scheduleListState.scrollToItem(0)
            scheduleUiState.pagerSplitWeeks.scrollToPage(scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.defaultDate.dayOfWeek.value - 1)
            scheduleUiState.onSelectWeek(
                if (scheduleState.currentNamedSchedule.scheduleUiDto.scheduleEntity.recurrence != null) {
                    scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.defaultDate.getCurrentWeek(
                        scheduleState.currentNamedSchedule.scheduleUiDto.scheduleEntity.startDate,
                        scheduleState.currentNamedSchedule.scheduleUiDto.scheduleEntity.recurrence
                    )
                } else 0
            )
        }

        LaunchedEffect(scheduleUiState.selectedDate) {
            val targetPage = ChronoUnit.DAYS.between(
                scheduleState.currentNamedSchedule.scheduleUiDto.scheduleEntity.startDate,
                scheduleUiState.selectedDate
            ).toInt()

            if (scheduleUiState.pagerDaysState.currentPage != targetPage) {
                scheduleUiState.pagerDaysState.scrollToPage(targetPage)
            }
        }

        LaunchedEffect(scheduleUiState.pagerDaysState.currentPage) {
            val newSelectedDate =
                scheduleState.currentNamedSchedule.scheduleUiDto.scheduleEntity.startDate.plusDays(
                    scheduleUiState.pagerDaysState.currentPage.toLong()
                )
            scheduleUiState.onSelectDate(newSelectedDate)

            val targetWeekIndex = ChronoUnit.WEEKS.between(
                scheduleState.currentNamedSchedule.scheduleUiDto.scheduleEntity.startDate.getFirstDayOfWeek(),
                newSelectedDate.getFirstDayOfWeek()
            ).toInt()

            if (scheduleUiState.pagerWeeksState.currentPage != targetWeekIndex) {
                scheduleUiState.pagerWeeksState.animateScrollToPage(targetWeekIndex)
            }
        }

        if (scheduleState.isSaved) {
            LaunchedEffect(currentDateTime) {
                scheduleViewModel.refreshScheduleState(
                    namedScheduleId = scheduleState.currentNamedSchedule.namedSchedule.namedScheduleEntity.id,
                    showLoading = false
                )
            }
        }
    }
}