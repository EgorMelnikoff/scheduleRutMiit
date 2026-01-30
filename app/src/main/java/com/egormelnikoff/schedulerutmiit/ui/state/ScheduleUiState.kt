package com.egormelnikoff.schedulerutmiit.ui.state

import androidx.annotation.Keep
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    val onSelectDate: (LocalDate) -> Unit
) {
    companion object {
        @Composable
        fun scheduleUiState(
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

                ScheduleUiState(
                    scheduleListState = scheduleListState,
                    pagerWeeksState = pagerWeeksState,
                    pagerDaysState = pagerDaysState,
                    selectedDate = selectedDate,
                    onSelectDate = { newDate ->
                        selectedDate = newDate
                    }
                )
            } else null
        }
    }
}