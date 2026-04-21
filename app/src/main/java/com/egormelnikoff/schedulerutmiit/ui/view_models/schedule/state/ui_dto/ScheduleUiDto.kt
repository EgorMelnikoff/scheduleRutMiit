package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.app.extension.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.Schedule
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.RecurrenceDto
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Keep
data class ScheduleUiDto(
    val scheduleEntity: ScheduleEntity,

    val periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>? = null,
    val nonPeriodicEvents: Map<LocalDate, List<Event>>? = null,
    val fullEventList: List<Pair<LocalDate, List<Event>>> = listOf(),

    val hiddenEvents: List<Event> = listOf(),
    val eventsExtraData: List<EventExtraData> = listOf(),
    val schedulePagerUiDto: SchedulePagerUiDto
) {
    companion object {
        operator fun invoke(
            schedule: Schedule
        ): ScheduleUiDto {
            val today = LocalDate.now()
            val splitEvents = schedule.events.partition { it.isHidden }

            var periodicEventsForCalendar: Map<Int, Map<DayOfWeek, List<Event>>>? = null
            var nonPeriodicEventsForCalendar: Map<LocalDate, List<Event>>? = null

            if (schedule.scheduleEntity.recurrence != null) {
                periodicEventsForCalendar = splitEvents.second.getPeriodicEvents(
                    schedule.scheduleEntity.recurrence.interval
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
                scheduleEntity = schedule.scheduleEntity,
            )

            val schedulePagerUiDto = SchedulePagerUiDto(
                today = today,
                startDate = schedule.scheduleEntity.startDate,
                endDate = schedule.scheduleEntity.endDate
            )

            return ScheduleUiDto(
                scheduleEntity = schedule.scheduleEntity,

                periodicEvents = periodicEventsForCalendar,
                schedulePagerUiDto = schedulePagerUiDto,
                nonPeriodicEvents = nonPeriodicEventsForCalendar,
                fullEventList = fullEventList,

                eventsExtraData = schedule.eventsExtraData,
                hiddenEvents = splitEvents.first
            )
        }

        private fun getFullEventsList(
            today: LocalDate,
            scheduleEntity: ScheduleEntity,
            periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
            nonPeriodicEventsList: List<Event>?
        ): List<Pair<LocalDate, List<Event>>> {
            val fullEventsList = when {
                periodicEvents == null && nonPeriodicEventsList == null -> emptyList()

                periodicEvents != null && scheduleEntity.recurrence != null -> {
                    val currentStartDate = maxOf(today, scheduleEntity.startDate)

                    val weeksNumbers = getWeekNumbers(
                        currentStartDate = currentStartDate,
                        startDate = scheduleEntity.startDate,
                        endDate = scheduleEntity.endDate,
                        recurrence = scheduleEntity.recurrence
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
            recurrence: RecurrenceDto
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