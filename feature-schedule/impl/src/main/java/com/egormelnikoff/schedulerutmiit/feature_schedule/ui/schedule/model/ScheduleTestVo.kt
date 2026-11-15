package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model

import androidx.compose.runtime.Immutable
import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.CalendarData
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getGroupedEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Recurrence
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.ScheduleWithEvents
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Immutable
data class ScheduleTestVo(
    val schedule: Schedule,
    val calendarData: CalendarData,

    val periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>? = null,
    val nonPeriodicEvents: Map<LocalDate, List<Event>>? = null,
    val fullEventList: Map<LocalDate, Map<String, List<Event>>> = mapOf(),
    val eventsExtraData: Map<Long, List<EventExtraData>> = mapOf(),

    val haveHiddenEvents: Boolean = false
)