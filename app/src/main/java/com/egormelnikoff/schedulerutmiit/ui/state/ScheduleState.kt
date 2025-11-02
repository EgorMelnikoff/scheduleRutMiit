package com.egormelnikoff.schedulerutmiit.ui.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleUiState
import java.time.LocalDate

data class ScheduleState(
    val scheduleListState: LazyListState,
    val pagerWeeksState: PagerState,
    val pagerDaysState: PagerState,
    val selectedDate: LocalDate,
    val onDateChange: (LocalDate) -> Unit,
    val expandedSchedulesMenu: Boolean,
    val onExpandSchedulesMenu: (Boolean) -> Unit
)

@Composable
fun rememberScheduleState(
    scheduleUiState: ScheduleUiState
): ScheduleState? {
    return if (scheduleUiState.currentScheduleData?.namedSchedule != null) {
        val scheduleListState = rememberLazyListState()
        val pagerDaysState = rememberPagerState(
            pageCount = { scheduleUiState.currentScheduleData.weeksCount.times(7) },
            initialPage = scheduleUiState.currentScheduleData.daysStartIndex
        )
        val pagerWeeksState = rememberPagerState(
            pageCount = { scheduleUiState.currentScheduleData.weeksCount },
            initialPage = scheduleUiState.currentScheduleData.weeksStartIndex
        )

        var selectedDate by remember(
            scheduleUiState.currentScheduleData.namedSchedule.namedScheduleEntity.apiId
        ) {
            mutableStateOf(
                scheduleUiState.currentScheduleData.defaultDate
            )
        }
        var expandedSchedulesMenu by remember { mutableStateOf(false) }

        ScheduleState(
            scheduleListState = scheduleListState,
            pagerWeeksState = pagerWeeksState,
            pagerDaysState = pagerDaysState,
            selectedDate = selectedDate,
            onDateChange = { newDate ->
                selectedDate = newDate
            },
            expandedSchedulesMenu = expandedSchedulesMenu,
            onExpandSchedulesMenu = { newValue ->
                expandedSchedulesMenu = newValue
            }
        )
    } else null

}