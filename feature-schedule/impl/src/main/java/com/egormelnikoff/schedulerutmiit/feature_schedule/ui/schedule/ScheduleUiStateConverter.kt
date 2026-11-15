package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.CalendarData
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.findDefault
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getGroupedEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Recurrence
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.EventVo
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.ScheduleVo
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.collections.forEach

class ScheduleUiStateConverter @Inject constructor() {
    fun toUiState(
        state: TestState
    ): ScheduleUiState {
        return ScheduleUiState(
            mode = state.mode.toVo(),
            isSaved = state.isSaved,
            isRefreshing = state.isRefreshing,
            time = state.time,
            namedSchedules = state.namedSchedules,
            namedSchedule = state.namedScheduleWithSchedules?.namedSchedule,
            scheduleVo = state.namedScheduleWithSchedules.toVo(),
            scheduleSheet = state.scheduleSheet,
            schedulesSheet = state.schedulesSheet,
            importSheet = state.importSheet,
            hideEventSheet = state.hideEventSheet,
            deleteEventSheet = state.deleteEventSheet,
            returnScheduleSheet = state.returnScheduleSheet,
            deleteScheduleSheet = state.deleteScheduleSheet
        )
    }

    private fun TestState.Mode.toVo(): ScheduleUiState.Mode {
        return when (this) {
            TestState.Mode.Content -> ScheduleUiState.Mode.Content
            TestState.Mode.Empty -> ScheduleUiState.Mode.Empty
            is TestState.Mode.Error -> ScheduleUiState.Mode.Error(this.appError)
            TestState.Mode.Loading -> ScheduleUiState.Mode.Loading
        }
    }


    private fun Event.toVo(
        eventsExtraData: Map<Long, List<EventExtraData>>?,
    ): EventVo {
        return EventVo(
            id = id,
            scheduleId = scheduleId,
            isHidden = isHidden,
            isCustomEvent = isCustomEvent,
            startDatetime = startDatetime,
            endDatetime = endDatetime,
            interval = interval,
            periodNumber = periodNumber,
            name = name,
            typeName = typeName,
            timeSlotName = timeSlotName,
            lecturers = lecturers,
            rooms = rooms,
            groups = groups,
            eventsExtraData = eventsExtraData?.get(id)?.associateBy { it.date }.orEmpty()
        )
    }

    private fun NamedScheduleWithSchedules?.toVo(): ScheduleVo? {
        val scheduleWithEvents = this?.schedulesWithEvents?.findDefault() ?: return null

        val splitEvents = scheduleWithEvents.events.partition { it.isHidden }

        var periodicEventsForCalendar: Map<Int, Map<DayOfWeek, List<Event>>>? = null
        var nonPeriodicEventsForCalendar: Map<LocalDate, List<Event>>? = null

        if (scheduleWithEvents.schedule.recurrence != null) {
            periodicEventsForCalendar = splitEvents.second.getPeriodicEvents(
                scheduleWithEvents.schedule.recurrence.interval
            )
        } else {
            nonPeriodicEventsForCalendar = splitEvents.second.groupBy {
                it.startDatetime.toLocalDate()
            }
        }

        val fullEventList = getFullEventsList(
            periodicEvents = periodicEventsForCalendar,
            nonPeriodicEventsList = splitEvents.second,
            schedule = scheduleWithEvents.schedule,
        )

        val calendarData = CalendarData(
            startDate = scheduleWithEvents.schedule.startDate,
            endDate = scheduleWithEvents.schedule.endDate
        )

        return ScheduleVo(
            schedule = scheduleWithEvents.schedule,

            periodicEvents = periodicEventsForCalendar,
            nonPeriodicEvents = nonPeriodicEventsForCalendar,
            calendarData = calendarData,
            fullEventList = fullEventList,
            eventsExtraData = scheduleWithEvents.eventsExtraData.groupBy { it.eventId },
            haveHiddenEvents = splitEvents.first.isNotEmpty()
        )
    }


    private fun getFullEventsList(
        schedule: Schedule,
        periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
        nonPeriodicEventsList: List<Event>?
    ): Map<LocalDate, Map<String, List<Event>>> {
        val today = LocalDate.now()

        val fullEventsList = when {
            periodicEvents == null && nonPeriodicEventsList == null -> emptyList()

            periodicEvents != null && schedule.recurrence != null ->
                periodicEvents.getFullPeriodicEventList(
                    schedule,
                    today
                )

            else -> nonPeriodicEventsList ?: emptyList()
        }

        return fullEventsList
            .asSequence()
            .filter { it.startDatetime.toLocalDate()?.isAfter(today.minusDays(1)) == true }
            .sortedBy { it.startDatetime }
            .groupBy { it.startDatetime.toLocalDate() }
            .mapValues {
                it.value.getGroupedEvents()
            }
    }


    private fun Map<Int, Map<DayOfWeek, List<Event>>>.getFullPeriodicEventList(
        schedule: Schedule,
        today: LocalDate
    ): List<Event> {
        val currentStartDate = maxOf(today, schedule.startDate)

        val weeksNumbers = getWeekNumbers(
            currentStartDate = currentStartDate,
            startDate = schedule.startDate,
            endDate = schedule.endDate,
            recurrence = schedule.recurrence!!
        )

        return buildList {
            var currentWeekStartDate = currentStartDate

            weeksNumbers.forEach { week ->
                val weekMap = this@getFullPeriodicEventList[week] ?: emptyMap()

                weekMap.values.forEach { eventsInDay ->
                    eventsInDay.forEach { event ->
                        val dayShift = event.startDatetime.dayOfWeek.value
                            .minus(currentStartDate.dayOfWeek.value)

                        val newDate = currentWeekStartDate
                            .plusDays(dayShift.toLong())

                        if (newDate >= schedule.startDate && newDate <= schedule.endDate) {
                            add(
                                event.copy(
                                    startDatetime = newDate.atTime(event.startDatetime.toLocalTime()),
                                    endDatetime = newDate.atTime(event.endDatetime.toLocalTime())
                                )
                            )
                        }
                    }
                }

                currentWeekStartDate = currentWeekStartDate.plusWeeks(1)
            }
        }
    }

    private fun getWeekNumbers(
        currentStartDate: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate,
        recurrence: Recurrence
    ): List<Int> {
        val weeksCount = ChronoUnit.WEEKS.between(
            startDate.getFirstDayOfWeek(),
            endDate.getFirstDayOfWeek()
        ).toInt() + 1

        val weeksRemaining = ChronoUnit.WEEKS.between(
            currentStartDate.getFirstDayOfWeek(),
            endDate.getFirstDayOfWeek()
        ).toInt()

        return (weeksCount - weeksRemaining..weeksCount).map { week ->
            ((week + recurrence.firstWeekNumber) % recurrence.interval).plus(1)
        }
    }
}