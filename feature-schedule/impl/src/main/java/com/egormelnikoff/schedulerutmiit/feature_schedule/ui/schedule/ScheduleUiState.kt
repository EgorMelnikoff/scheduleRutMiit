package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import androidx.compose.runtime.Immutable
import com.egormelnikoff.schedulerutmiit.core.common.error.AppError
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.ScheduleVo
import java.time.LocalDateTime

@Immutable
data class ScheduleUiState(
    val isSaved: Boolean,
    val isRefreshing: Boolean,
    val mode: Mode,
    val time: LocalDateTime,
    val namedSchedules: List<NamedSchedule>,
    val namedSchedule: NamedSchedule?,
    val scheduleVo: ScheduleVo?,
    val scheduleSheet: Boolean,
    val schedulesSheet: Boolean,
    val importSheet: Boolean,
    val hideEventSheet: Long?,
    val deleteEventSheet: Long?,
    val returnScheduleSheet: Boolean,
    val deleteScheduleSheet: Boolean,
) {
    sealed interface Mode {
        data object Empty : Mode
        data class Error(
            val appError: AppError
        ) : Mode

        data object Loading : Mode
        data object Content : Mode
    }
}