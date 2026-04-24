package com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state

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
import com.egormelnikoff.schedulerutmiit.schedule.view_model.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.view_model.state.ScheduleState
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
        operator fun invoke(
            namedScheduleState: NamedScheduleState,
            scheduleState: ScheduleState
        ): ScheduleUiState? {
            return if (scheduleState.scheduleUiDto?.schedulePagerUiDto != null) {
                val scheduleListState = rememberLazyListState()
                val pagerDaysState = rememberPagerState(
                    pageCount = { requireNotNull(scheduleState.scheduleUiDto).schedulePagerUiDto.daysCount },
                    initialPage = requireNotNull(scheduleState.scheduleUiDto).schedulePagerUiDto.daysStartIndex
                )
                val pagerWeeksState = rememberPagerState(
                    pageCount = { requireNotNull(scheduleState.scheduleUiDto).schedulePagerUiDto.weeksCount },
                    initialPage = requireNotNull(scheduleState.scheduleUiDto).schedulePagerUiDto.weeksStartIndex
                )

                var selectedDate by remember(
                    namedScheduleState.namedSchedule?.namedScheduleEntity?.apiId
                ) {
                    mutableStateOf(
                        requireNotNull(scheduleState.scheduleUiDto).schedulePagerUiDto.defaultDate
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