package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.CalendarData
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.findDefault
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.findEventExtra
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Recurrence
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.EventTimeGroupVo
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.EventVo
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.ScheduleVo
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class ScheduleUiStateConverter @Inject constructor() {

    private data class ScheduleCacheKey(
        val namedScheduleWithSchedules: NamedScheduleWithSchedules?,
        val eventExtraPolicy: EventExtraPolicy,
        val today: LocalDate
    )

    private var scheduleCache: Pair<ScheduleCacheKey, ScheduleVo>? = null

    fun toUiState(
        state: ScheduleState
    ): ScheduleUiState {
        val today = state.time.toLocalDate()

        return ScheduleUiState(
            mode = state.mode.toUi(),
            isSaved = state.isSaved,
            isRefreshing = state.isRefreshing,
            time = state.time,
            namedSchedules = state.namedSchedules,
            namedSchedule = state.namedScheduleWithSchedules?.namedSchedule,
            schedules = state.namedScheduleWithSchedules?.schedulesWithEvents?.map { it.schedule }
                .orEmpty(),
            scheduleVo = state.namedScheduleWithSchedules.toScheduleVo(
                eventExtraPolicy = state.eventExtraPolicy,
                today = today
            ),
            eventView = state.eventView,
            scheduleView = state.scheduleView,
            eventsCountView = state.eventsCountView,
            schedulesSheet = state.schedulesSheet,
            scheduleSheet = state.scheduleSheet,
            importSheet = state.importSheet,
            returnScheduleSheet = state.returnScheduleSheet
        )
    }

    private fun ScheduleState.Mode.toUi(): ScheduleUiState.Mode {
        return when (this) {
            ScheduleState.Mode.Content -> ScheduleUiState.Mode.Content
            ScheduleState.Mode.Empty -> ScheduleUiState.Mode.Empty
            is ScheduleState.Mode.Error -> ScheduleUiState.Mode.Error(this.appError)
            ScheduleState.Mode.Loading -> ScheduleUiState.Mode.Loading
        }
    }

    private fun NamedScheduleWithSchedules?.toScheduleVo(
        eventExtraPolicy: EventExtraPolicy,
        today: LocalDate
    ): ScheduleVo? {
        val scheduleWithEvents = this?.schedulesWithEvents?.findDefault() ?: return null

        val cacheKey = ScheduleCacheKey(this, eventExtraPolicy, today)
        scheduleCache?.takeIf { it.first == cacheKey }?.let { return it.second }

        return scheduleWithEvents.toVo(eventExtraPolicy, today).also { scheduleVo ->
            scheduleCache = cacheKey to scheduleVo
        }
    }

    private fun ScheduleWithEvents.toVo(
        eventExtraPolicy: EventExtraPolicy,
        today: LocalDate
    ): ScheduleVo {
        val visibleEvents = events.filter { !it.isHidden }

        val eventsByDate = visibleEvents
            .expandOccurrences(schedule)
            .sortedBy { it.startDatetime }
            .groupBy { it.startDatetime.toLocalDate() }
            .mapValues { (_, events) ->
                events.toTimeGroups(eventExtraPolicy, eventsExtraData)
            }
            .toMap()

        return ScheduleVo(
            schedule = schedule,
            calendarData = CalendarData(
                startDate = schedule.startDate,
                endDate = schedule.endDate
            ),
            eventsByDate = eventsByDate,
            fullEventList = eventsByDate.filter { (date, _) -> !date.isBefore(today) },
            eventsCountByDate = eventsByDate.mapValues { (_, groups) -> groups.size }.toMap(),
            weekByFirstDayOfWeek = schedule.recurrence?.let { recurrence ->
                getWeekByFirstDayOfWeek(
                    startDate = schedule.startDate,
                    endDate = schedule.endDate,
                    recurrence = recurrence
                )
            }.orEmpty(),
            haveHiddenEvents = visibleEvents.size != events.size
        )
    }

    private fun List<Event>.expandOccurrences(schedule: Schedule): List<Event> {
        val recurrence = schedule.recurrence ?: return this

        val periodicEvents = getPeriodicEvents(recurrence.interval)

        val weeksCount = ChronoUnit.WEEKS.between(
            schedule.startDate.getFirstDayOfWeek(),
            schedule.endDate.getFirstDayOfWeek()
        ).toInt() + 1

        return buildList {
            var currentWeekStartDate = schedule.startDate

            for (week in 1..weeksCount) {
                val weekNumber = ((week + recurrence.firstWeekNumber) % recurrence.interval) + 1
                val weekMap = periodicEvents[weekNumber].orEmpty()

                weekMap.values.forEach { eventsInDay ->
                    eventsInDay.forEach { event ->
                        val dayShift = event.startDatetime.dayOfWeek.value
                            .minus(schedule.startDate.dayOfWeek.value)

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

    private fun List<Event>.toTimeGroups(
        eventExtraPolicy: EventExtraPolicy,
        eventsExtraData: List<EventExtraData>
    ): List<EventTimeGroupVo> {
        val extrasByEventId = eventsExtraData.groupBy { it.eventId }

        return sortedBy { it.startDatetime }
            .groupBy {
                it.startDatetime.toLocalTimeWithTimeZone() to it.endDatetime.toLocalTimeWithTimeZone()
            }
            .map { (time, events) ->
                EventTimeGroupVo(
                    timeSlotName = events.first().timeSlotName,
                    startTime = time.first,
                    endTime = time.second,
                    events = events.map { it.toVo(eventExtraPolicy, extrasByEventId) }
                )
            }
    }

    private fun Event.toVo(
        eventExtraPolicy: EventExtraPolicy,
        extrasByEventId: Map<Long, List<EventExtraData>>
    ): EventVo {
        val date = startDatetime.toLocalDate()
        val extra = extrasByEventId.findEventExtra(eventExtraPolicy, id, date)

        return EventVo(
            id = id,
            scheduleId = scheduleId,
            isCustomEvent = isCustomEvent,
            startDatetime = startDatetime,
            endDatetime = endDatetime,
            name = name,
            typeName = typeName,
            timeSlotName = timeSlotName,
            lecturers = lecturers,
            rooms = rooms,
            groups = groups,
            comment = extra?.comment?.takeIf { it.isNotBlank() }
                ?.replace(Regex("\\s+"), " "),
            tag = extra?.tag,
            dialogDate = if (eventExtraPolicy == EventExtraPolicy.BY_DATES) date else null
        )
    }

    private fun getWeekByFirstDayOfWeek(
        startDate: LocalDate,
        endDate: LocalDate,
        recurrence: Recurrence
    ): Map<LocalDate, Int> {
        val firstWeekStartDate = startDate.getFirstDayOfWeek()

        val weeksCount = ChronoUnit.WEEKS.between(
            firstWeekStartDate,
            endDate.getFirstDayOfWeek()
        ).toInt() + 1

        return (0 until weeksCount).associate { weekIndex ->
            firstWeekStartDate.plusWeeks(weekIndex.toLong()) to
                    ((weekIndex + 1 + recurrence.firstWeekNumber) % recurrence.interval) + 1
        }
    }
}
