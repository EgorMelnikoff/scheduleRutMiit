package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import com.egormelnikoff.schedulerutmiit.core.common.error.AppError
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class ScheduleState(
    val mode: Mode = Mode.Loading,
    val isSaved: Boolean = true,
    val isRefreshing: Boolean = false,
    val time: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS),
    val namedSchedules: List<NamedSchedule> = emptyList(),
    val namedScheduleWithSchedules: NamedScheduleWithSchedules? = null,

    val eventExtraPolicy: EventExtraPolicy = EventExtraPolicy.DEFAULT,
    val eventView: EventView = EventView(
        groupsVisible = true,
        roomsVisible = true,
        lecturersVisible = true,
        tagVisible = true,
        commentVisible = true
    ),
    val scheduleView: ScheduleView = ScheduleView.CALENDAR,
    val eventsCountView: EventsCountView = EventsCountView.DETAILS,

    val schedulesSheet: Boolean = false,
    val scheduleSheet: Boolean = false,
    val importSheet: Boolean = false,
    val returnScheduleSheet: Boolean = false
) {

    fun dataMode(): Mode {
        return if (namedScheduleWithSchedules != null) Mode.Content else Mode.Empty
    }

    sealed interface Mode {
        data object Empty : Mode
        data class Error(
            val appError: AppError
        ) : Mode

        data object Loading : Mode
        data object Content : Mode
    }
}
