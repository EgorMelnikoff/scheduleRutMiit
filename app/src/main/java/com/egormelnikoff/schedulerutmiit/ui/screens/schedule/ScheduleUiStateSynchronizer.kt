package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.CurrentState
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ScheduleState
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
    if (scheduleState.scheduleUiDto?.scheduleEntity != null && scheduleUiState != null) {
        LaunchedEffect(
            namedScheduleState.namedSchedule?.namedScheduleEntity?.apiId,
            scheduleState.scheduleUiDto.scheduleEntity.timetableId
        ) {
            scheduleUiState.pagerDaysState.scrollToPage(
                scheduleState.scheduleUiDto.schedulePagerUiDto.daysStartIndex
            )
            scheduleUiState.scheduleListState.scrollToItem(0)
        }

        LaunchedEffect(scheduleUiState.selectedDate) {
            val targetPage = ChronoUnit.DAYS.between(
                scheduleState.scheduleUiDto.scheduleEntity.startDate,
                scheduleUiState.selectedDate
            ).toInt()

            if (scheduleUiState.pagerDaysState.currentPage != targetPage) {
                scheduleUiState.pagerDaysState.scrollToPage(targetPage)
            }
        }

        LaunchedEffect(scheduleUiState.pagerDaysState.currentPage) {
            val newSelectedDate =
                scheduleState.scheduleUiDto.scheduleEntity.startDate.plusDays(
                    scheduleUiState.pagerDaysState.currentPage.toLong()
                )
            scheduleUiState.onSelectDate(newSelectedDate)

            val targetWeekIndex = ChronoUnit.WEEKS.between(
                scheduleState.scheduleUiDto.scheduleEntity.startDate.getFirstDayOfWeek(),
                newSelectedDate.getFirstDayOfWeek()
            ).toInt()

            if (scheduleUiState.pagerWeeksState.currentPage != targetWeekIndex) {
                scheduleUiState.pagerWeeksState.animateScrollToPage(targetWeekIndex)
            }
        }

        LaunchedEffect(currentDateTime) {
            if (currentState.isSaved) {
                scheduleViewModel.refreshScheduleState(
                    namedScheduleId = namedScheduleState.namedSchedule?.namedScheduleEntity?.id,
                    showLoading = false
                )
            }
        }
    }
}
