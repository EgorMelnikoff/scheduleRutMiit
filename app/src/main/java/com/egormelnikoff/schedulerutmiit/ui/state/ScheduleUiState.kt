package com.egormelnikoff.schedulerutmiit.ui.state

import androidx.annotation.Keep
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.egormelnikoff.schedulerutmiit.app.extension.getCurrentWeek
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ScheduleState
import java.time.LocalDate

@Keep
data class ScheduleUiState(
    val scheduleListState: LazyListState,

    val pagerWeeksState: PagerState,
    val pagerDaysState: PagerState,
    val selectedDate: LocalDate,
    val onSelectDate: (LocalDate) -> Unit,

    val pagerSplitWeeks: PagerState,
    val selectedWeek: Int,
    val onSelectWeek: (Int) -> Unit,
) {
    companion object {
        @Composable
        operator fun invoke(
            scheduleState: ScheduleState
        ): ScheduleUiState? {
            return if (scheduleState.currentNamedSchedule?.namedSchedule != null && scheduleState.currentNamedSchedule.scheduleUiDto?.schedulePagerUiDto != null) {
                val scheduleListState = rememberLazyListState()
                val pagerDaysState = rememberPagerState(
                    pageCount = { scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.daysCount },
                    initialPage = scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.daysStartIndex
                )
                val pagerWeeksState = rememberPagerState(
                    pageCount = { scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.weeksCount },
                    initialPage = scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.weeksStartIndex
                )

                var selectedDate by remember(
                    scheduleState.currentNamedSchedule.namedSchedule.namedScheduleEntity.apiId
                ) {
                    mutableStateOf(
                        scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.defaultDate
                    )
                }

                var selectedWeek by remember {
                    mutableIntStateOf(
                        if (scheduleState.currentNamedSchedule.scheduleUiDto.scheduleEntity.recurrence != null) {
                            scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.defaultDate.getCurrentWeek(
                                scheduleState.currentNamedSchedule.scheduleUiDto.scheduleEntity.startDate,
                                scheduleState.currentNamedSchedule.scheduleUiDto.scheduleEntity.recurrence
                            )
                        } else 0
                    )
                }

                val pagerSplitWeeks = rememberPagerState(
                    initialPage = scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.defaultDate.dayOfWeek.value - 1,
                    pageCount = { 7 }
                )

                ScheduleUiState(
                    scheduleListState = scheduleListState,

                    pagerWeeksState = pagerWeeksState,
                    pagerDaysState = pagerDaysState,
                    selectedDate = selectedDate,
                    onSelectDate = { newDate ->
                        selectedDate = newDate
                    },

                    pagerSplitWeeks = pagerSplitWeeks,
                    selectedWeek = selectedWeek,
                    onSelectWeek = { newWeek ->
                        selectedWeek = newWeek
                    }
                )
            } else null
        }
    }
}