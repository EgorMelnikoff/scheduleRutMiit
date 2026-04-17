package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.Schedule
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.RecurrenceDto
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Keep
data class ScheduleUiDto(
    val scheduleEntity: ScheduleEntity,
    val periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>? = null,
    val nonPeriodicEvents: Map<LocalDate, List<Event>>? = null,
    val fullEventList: List<Pair<LocalDate, List<Event>>> = listOf(),
    val hiddenEvents: List<Event> = listOf(),
    val eventsExtraData: List<EventExtraData> = listOf(),
    val schedulePagerUiDto: SchedulePagerUiDto,
    val reviewUiDto: ReviewUiDto
) {
    companion object {
        operator fun invoke(
            schedule: Schedule
        ): ScheduleUiDto {
            val today = LocalDateTime.now()
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
                today = today.toLocalDate(),
                periodicEvents = periodicEventsForCalendar,
                nonPeriodicEventsList = splitEvents.second,
                scheduleEntity = schedule.scheduleEntity,
            )

            val schedulePagerUiDto = SchedulePagerUiDto(
                today = today.toLocalDate(),
                startDate = schedule.scheduleEntity.startDate,
                endDate = schedule.scheduleEntity.endDate
            )

            val reviewUiDto = ReviewUiDto(
                date = today,
                scheduleEntity = schedule.scheduleEntity,
                periodicEvents = periodicEventsForCalendar,
                nonPeriodicEvents = nonPeriodicEventsForCalendar
            )

            return ScheduleUiDto(
                scheduleEntity = schedule.scheduleEntity,

                periodicEvents = periodicEventsForCalendar,
                schedulePagerUiDto = schedulePagerUiDto,
                nonPeriodicEvents = nonPeriodicEventsForCalendar,
                fullEventList = fullEventList,

                eventsExtraData = schedule.eventsExtraData,
                hiddenEvents = splitEvents.first,
                reviewUiDto = reviewUiDto
            )
        }

        fun List<Event>.getPeriodicEvents(
            interval: Int
        ): Map<Int, Map<DayOfWeek, List<Event>>> {
            return buildMap {
                for (week in 1..interval) {
                    val eventsForWeek = this@getPeriodicEvents.filter { event ->
                        val rule = event.recurrenceRule ?: return@filter false
                        (rule.interval == 1 || event.periodNumber == week)
                    }
                    if (eventsForWeek.isNotEmpty()) {
                        this[week] = eventsForWeek.groupBy { it.startDatetime.dayOfWeek }
                    } else {
                        this[week] = emptyMap()
                    }
                }
            }
        }

        private fun getFullEventsList(
            today: LocalDate,
            scheduleEntity: ScheduleEntity,
            periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
            nonPeriodicEventsList: List<Event>?
        ): List<Pair<LocalDate, List<Event>>> {
            var fullEventsList = listOf<Event>()
            when {
                (periodicEvents == null && nonPeriodicEventsList == null) -> return listOf()
                (periodicEvents != null && scheduleEntity.recurrence != null) -> {
                    val currentStartDate = maxOf(today, scheduleEntity.startDate)
                    val events = buildList {
                        val weeksNumbers = getWeekNumbers(
                            currentStartDate = currentStartDate,
                            startDate = scheduleEntity.startDate,
                            endDate = scheduleEntity.endDate,
                            recurrence = scheduleEntity.recurrence
                        )
                        weeksNumbers.forEachIndexed { index, week ->
                            val eventsInWeek = periodicEvents[week]?.values.orEmpty().flatten()
                            val currentWeekStartDate = currentStartDate.plusWeeks(index.toLong())
                            eventsInWeek.forEach { event ->
                                val daysToAdd =
                                    event.startDatetime.dayOfWeek.value - currentStartDate.dayOfWeek.value
                                val newEventDate = currentWeekStartDate.plusDays(daysToAdd.toLong())
                                val newEvent = event.copy(
                                    startDatetime = newEventDate.atTime(event.startDatetime.toLocalTime()),
                                    endDatetime = newEventDate.atTime(event.endDatetime.toLocalTime()),
                                )
                                add(newEvent)
                            }
                        }
                    }
                    fullEventsList = events
                }

                (nonPeriodicEventsList != null) -> {
                    fullEventsList = nonPeriodicEventsList
                }
            }

            return fullEventsList
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
                ((week + recurrence.firstWeekNumber) % recurrence.interval)
                    .plus(1)
            }
        }
    }
}