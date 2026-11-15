package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import com.egormelnikoff.schedulerutmiit.core.common.error.AppError
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import java.time.LocalDateTime

data class TestState(
    val isSaved: Boolean = true,
    val isRefreshing: Boolean = false,
    val mode: Mode = Mode.Empty,
    val time: LocalDateTime = LocalDateTime.of(0, 0, 0, 0, 0),
    val namedSchedules: List<NamedSchedule> = emptyList(),
    val namedScheduleWithSchedules: NamedScheduleWithSchedules? = null,

    val scheduleSheet: Boolean = false,
    val schedulesSheet: Boolean = false,
    val importSheet: Boolean = false,
    val hideEventSheet: Long? = null,
    val deleteEventSheet: Long? = null,
    val returnScheduleSheet: Boolean = false,
    val deleteScheduleSheet: Boolean = false,
) {

    fun toError(appError: AppError) = copy(
        mode = Mode.Error(appError)
    )

    fun toLoading() = copy(
        mode = Mode.Loading
    )

    fun toEmpty() = copy(
        mode = Mode.Empty
    )

    fun toContent() = copy(
        isRefreshing = false,
        mode = Mode.Content
    )

    sealed interface Mode {
        data object Empty : Mode
        data class Error(
            val appError: AppError
        ) : Mode

        data object Loading : Mode
        data object Content : Mode
    }
}