package com.egormelnikoff.schedulerutmiit.schedule.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.CurrentState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ScheduleState
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun ScheduleUiStateSynchronizer(
    scheduleUiState: ScheduleUiState?,
    currentState: CurrentState,
    scheduleState: ScheduleState,
    namedScheduleState: NamedScheduleState,
    currentDateTime: LocalDateTime,
    scheduleViewModel: ScheduleViewModel,
) {
    if (scheduleState.scheduleUiDto?.schedule != null && scheduleUiState != null) {
        LaunchedEffect(
            namedScheduleState.namedScheduleWithSchedules?.namedSchedule?.apiId,
            scheduleState.scheduleUiDto.schedule.timetableId
        ) {
            scheduleUiState.pagerDaysState.scrollToPage(
                scheduleState.scheduleUiDto.schedulePagerUiDto.daysStartIndex
            )
            scheduleUiState.scheduleListState.scrollToItem(0)
        }

        LaunchedEffect(scheduleUiState.selectedDate) {
            val targetPage = ChronoUnit.DAYS.between(
                scheduleState.scheduleUiDto.schedule.startDate,
                scheduleUiState.selectedDate
            ).toInt()

            if (scheduleUiState.pagerDaysState.currentPage != targetPage) {
                scheduleUiState.pagerDaysState.scrollToPage(targetPage)
            }
        }

        LaunchedEffect(scheduleUiState.pagerDaysState.currentPage) {
            val newSelectedDate =
                scheduleState.scheduleUiDto.schedule.startDate.plusDays(
                    scheduleUiState.pagerDaysState.currentPage.toLong()
                )
            scheduleUiState.onSelectDate(newSelectedDate)

            val targetWeekIndex = ChronoUnit.WEEKS.between(
                scheduleState.scheduleUiDto.schedule.startDate.getFirstDayOfWeek(),
                newSelectedDate.getFirstDayOfWeek()
            ).toInt()

            if (scheduleUiState.pagerWeeksState.currentPage != targetWeekIndex) {
                scheduleUiState.pagerWeeksState.animateScrollToPage(targetWeekIndex)
            }
        }

        LaunchedEffect(currentDateTime) {
            if (currentState.isSaved) {
                scheduleViewModel.refreshScheduleState(
                    namedScheduleId = namedScheduleState.namedScheduleWithSchedules?.namedSchedule?.id,
                    showLoading = false
                )
            }
        }
    }
}
