package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import androidx.compose.runtime.Immutable
import com.egormelnikoff.schedulerutmiit.core.common.error.AppError
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.ScheduleVo
import java.time.LocalDateTime

@Immutable
data class ScheduleUiState(
    val mode: Mode,
    val isSaved: Boolean,
    val isRefreshing: Boolean,
    val time: LocalDateTime,

    val namedSchedules: List<NamedSchedule>,
    val namedSchedule: NamedSchedule?,
    val schedules: List<Schedule>,
    val scheduleVo: ScheduleVo?,

    val eventView: EventView,
    val scheduleView: ScheduleView,
    val eventsCountView: EventsCountView,

    val schedulesSheet: Boolean,
    val scheduleSheet: Boolean,
    val importSheet: Boolean,
    val returnScheduleSheet: Boolean,
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
