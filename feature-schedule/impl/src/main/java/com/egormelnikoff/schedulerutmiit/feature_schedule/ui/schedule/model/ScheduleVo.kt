package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model

import androidx.compose.runtime.Immutable
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.CalendarData
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import java.time.LocalDate

@Immutable
internal data class ScheduleVo(
    val schedule: Schedule,
    val calendarData: CalendarData,

    val eventsByDate: Map<LocalDate, List<EventTimeGroupVo>>,
    val fullEventList: Map<LocalDate, List<EventTimeGroupVo>>,
    val eventsCountByDate: Map<LocalDate, Int>,
    val weekByFirstDayOfWeek: Map<LocalDate, Int>,

    val haveHiddenEvents: Boolean
)
