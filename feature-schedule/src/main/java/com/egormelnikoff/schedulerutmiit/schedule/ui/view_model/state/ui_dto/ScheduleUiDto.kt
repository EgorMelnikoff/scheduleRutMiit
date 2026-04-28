package com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.Recurrence
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getPeriodicEvents
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class ScheduleUiDto(
    val schedule: Schedule,

    val periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>? = null,
    val nonPeriodicEvents: Map<LocalDate, List<Event>>? = null,
    val fullEventList: List<Pair<LocalDate, List<Event>>> = listOf(),

    val hiddenEvents: List<Event> = listOf(),
    val eventsExtraData: List<EventExtraData> = listOf(),
    val schedulePagerUiDto: SchedulePagerUiDto
) {
    companion object {
        operator fun invoke(
            scheduleWithEvents: ScheduleWithEvents
        ): ScheduleUiDto {
            val today = LocalDate.now()
            val splitEvents = scheduleWithEvents.events.partition { it.isHidden }

            var periodicEventsForCalendar: Map<Int, Map<DayOfWeek, List<Event>>>? = null
            var nonPeriodicEventsForCalendar: Map<LocalDate, List<Event>>? = null

            if (scheduleWithEvents.schedule.recurrence != null) {
                periodicEventsForCalendar = splitEvents.second.getPeriodicEvents(
                    requireNotNull(scheduleWithEvents.schedule.recurrence).interval
                )
            } else {
                nonPeriodicEventsForCalendar = splitEvents.second.groupBy {
                    it.startDatetime.toLocalDate()
                }
            }

            val fullEventList = getFullEventsList(
                today = today,
                periodicEvents = periodicEventsForCalendar,
                nonPeriodicEventsList = splitEvents.second,
                schedule = scheduleWithEvents.schedule,
            )

            val schedulePagerUiDto = SchedulePagerUiDto(
                today = today,
                startDate = scheduleWithEvents.schedule.startDate,
                endDate = scheduleWithEvents.schedule.endDate
            )

            return ScheduleUiDto(
                schedule = scheduleWithEvents.schedule,

                periodicEvents = periodicEventsForCalendar,
                schedulePagerUiDto = schedulePagerUiDto,
                nonPeriodicEvents = nonPeriodicEventsForCalendar,
                fullEventList = fullEventList,

                eventsExtraData = scheduleWithEvents.eventsExtraData,
                hiddenEvents = splitEvents.first
            )
        }

        private fun getFullEventsList(
            today: LocalDate,
            schedule: Schedule,
            periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
            nonPeriodicEventsList: List<Event>?
        ): List<Pair<LocalDate, List<Event>>> {
            val fullEventsList = when {
                periodicEvents == null && nonPeriodicEventsList == null -> emptyList()

                periodicEvents != null && schedule.recurrence != null -> {
                    val currentStartDate = maxOf(today, schedule.startDate)

                    val weeksNumbers = getWeekNumbers(
                        currentStartDate = currentStartDate,
                        startDate = schedule.startDate,
                        endDate = schedule.endDate,
                        recurrence = requireNotNull(schedule.recurrence)
                    )

                    buildList {
                        var currentWeekStartDate = currentStartDate

                        weeksNumbers.forEach { week ->
                            val weekMap = periodicEvents[week] ?: emptyMap()

                            weekMap.values.forEach { eventsInDay ->
                                eventsInDay.forEach { event ->
                                    val dayShift = event.startDatetime.dayOfWeek.value
                                        .minus(currentStartDate.dayOfWeek.value)

                                    val newDate = currentWeekStartDate
                                        .plusDays(dayShift.toLong())

                                    add(
                                        event.copy(
                                            startDatetime = newDate.atTime(event.startDatetime.toLocalTime()),
                                            endDatetime = newDate.atTime(event.endDatetime.toLocalTime())
                                        )
                                    )
                                }
                            }

                            currentWeekStartDate = currentWeekStartDate.plusWeeks(1)
                        }
                    }
                }

                else -> nonPeriodicEventsList ?: emptyList()
            }

            return fullEventsList
                .asSequence()
                .filter { it.startDatetime.toLocalDate()?.isAfter(today.minusDays(1)) == true }
                .sortedBy { it.startDatetime }
                .groupBy { it.startDatetime.toLocalDate() }
                .toList()
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
}