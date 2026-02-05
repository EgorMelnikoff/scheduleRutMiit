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
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState
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
            return if (scheduleState.currentNamedScheduleData?.namedSchedule != null && scheduleState.currentNamedScheduleData.scheduleData?.schedulePagerData != null) {
                val scheduleListState = rememberLazyListState()
                val pagerDaysState = rememberPagerState(
                    pageCount = { scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.daysCount },
                    initialPage = scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.daysStartIndex
                )
                val pagerWeeksState = rememberPagerState(
                    pageCount = { scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.weeksCount },
                    initialPage = scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.weeksStartIndex
                )

                var selectedDate by remember(
                    scheduleState.currentNamedScheduleData.namedSchedule.namedScheduleEntity.apiId
                ) {
                    mutableStateOf(
                        scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.defaultDate
                    )
                }

                var selectedWeek by remember {
                    mutableIntStateOf(
                        if (scheduleState.currentNamedScheduleData.scheduleData.scheduleEntity?.recurrence != null) {
                            scheduleState.currentNamedScheduleData.scheduleData.scheduleEntity.recurrence.currentNumber
                        } else 0
                    )
                }

                val pagerSplitWeeks = rememberPagerState(
                    initialPage = scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.defaultDate.dayOfWeek.value - 1,
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