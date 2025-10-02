package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule

import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calculateFirstDayOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class ScheduleData(
    val namedSchedule: NamedScheduleFormatted? = null,
    val settledScheduleEntity: ScheduleEntity? = null,

    val periodicEventsForCalendar: Map<Int, Map<DayOfWeek, List<Event>>>? = null,
    val nonPeriodicEventsForCalendar: Map<LocalDate, List<Event>>? = null,
    val eventForList: List<Pair<LocalDate, List<Event>>> = listOf(),
    val eventsExtraData: List<EventExtraData> = listOf(),
    val hiddenEvents: List<Event> = listOf(),

    val defaultDate: LocalDate,
    val daysStartIndex: Int,
    val weeksStartIndex: Int,
    val weeksCount: Int
) {
    companion object {
        fun calculateScheduleData(
            namedSchedule: NamedScheduleFormatted?,
            scheduleId: Long?
        ): ScheduleData? {
            val scheduleFormatted = findCurrentSchedule(namedSchedule, scheduleId)
            return if (scheduleFormatted != null) {
                val today = LocalDate.now()
                val weeksCount = ChronoUnit.WEEKS.between(
                    calculateFirstDayOfWeek(scheduleFormatted.scheduleEntity.startDate),
                    calculateFirstDayOfWeek(scheduleFormatted.scheduleEntity.endDate)
                ).plus(1).toInt()

                val defaultParams = calculateDefaultParams(
                    today = today,
                    weeksCount = weeksCount,
                    scheduleEntity = scheduleFormatted.scheduleEntity
                )
                val scheduleWithoutHiddenEvents = scheduleFormatted.copy(
                    events = scheduleFormatted.events.filter { !it.isHidden }
                )
                val hiddenEvents = scheduleFormatted.events.filter { it.isHidden }

                if (scheduleWithoutHiddenEvents.scheduleEntity.recurrence != null) {
                    val periodicEventsForCalendar =
                        calculatePeriodicEventsForCalendar(scheduleWithoutHiddenEvents)
                    val eventsForList = calculateEventsForList(
                        today = today,
                        periodicEventsForCalendar = periodicEventsForCalendar,
                        nonPeriodicEvents = null,
                        scheduleEntity = scheduleWithoutHiddenEvents.scheduleEntity,
                    )
                    ScheduleData(
                        namedSchedule = namedSchedule,
                        settledScheduleEntity = scheduleFormatted.scheduleEntity,
                        periodicEventsForCalendar = periodicEventsForCalendar,
                        nonPeriodicEventsForCalendar = null,
                        eventsExtraData = scheduleWithoutHiddenEvents.eventsExtraData,
                        eventForList = eventsForList,
                        hiddenEvents = hiddenEvents,
                        weeksCount = weeksCount,
                        defaultDate = defaultParams.first,
                        weeksStartIndex = defaultParams.second,
                        daysStartIndex = defaultParams.third
                    )
                } else {
                    val nonPeriodicEventsForCalendar = scheduleWithoutHiddenEvents.events
                        .groupBy { it.startDatetime!!.toLocalDate() }
                    val eventsForList = calculateEventsForList(
                        today = today,
                        periodicEventsForCalendar = null,
                        nonPeriodicEvents = scheduleFormatted.events,
                        scheduleEntity = scheduleWithoutHiddenEvents.scheduleEntity,
                    )
                    ScheduleData(
                        namedSchedule = namedSchedule,
                        settledScheduleEntity = scheduleFormatted.scheduleEntity,
                        periodicEventsForCalendar = null,
                        nonPeriodicEventsForCalendar = nonPeriodicEventsForCalendar,
                        eventsExtraData = scheduleWithoutHiddenEvents.eventsExtraData,
                        eventForList = eventsForList,
                        hiddenEvents = hiddenEvents,
                        weeksCount = weeksCount,
                        defaultDate = defaultParams.first,
                        weeksStartIndex = defaultParams.second,
                        daysStartIndex = defaultParams.third
                    )
                }
            } else null
        }

        private fun findCurrentSchedule(
            namedSchedule: NamedScheduleFormatted?,
            scheduleId: Long?
        ): ScheduleFormatted? {
            if (namedSchedule == null) return null
            if (scheduleId == null) {
                val schedule = namedSchedule.schedules.find { it.scheduleEntity.isDefault }
                    ?: namedSchedule.schedules.firstOrNull()
                return schedule
            }
            return namedSchedule.schedules.firstOrNull { s -> s.scheduleEntity.id == scheduleId }
        }

        private fun calculateDefaultParams(
            today: LocalDate,
            weeksCount: Int,
            scheduleEntity: ScheduleEntity
        ): Triple<LocalDate, Int, Int> {
            val defaultDate: LocalDate
            val weeksStartIndex: Int
            val daysStartIndex: Int

            if (today in scheduleEntity.startDate..scheduleEntity.endDate) {
                weeksStartIndex = abs(
                    ChronoUnit.WEEKS.between(
                        calculateFirstDayOfWeek(scheduleEntity.startDate),
                        calculateFirstDayOfWeek(today)
                    ).toInt()
                )
                daysStartIndex = abs(
                    ChronoUnit.DAYS.between(
                        scheduleEntity.startDate,
                        today
                    ).toInt()
                )
                defaultDate = today
            } else if (today < scheduleEntity.startDate) {
                weeksStartIndex = 0
                daysStartIndex = 0
                defaultDate = scheduleEntity.startDate
            } else {
                weeksStartIndex = weeksCount
                daysStartIndex = weeksCount * 7
                defaultDate = scheduleEntity.endDate
            }
            return Triple(defaultDate, weeksStartIndex, daysStartIndex)
        }

        private fun calculatePeriodicEventsForCalendar(
            currentSchedule: ScheduleFormatted
        ): Map<Int, Map<DayOfWeek, List<Event>>> {
            return buildMap {
                for (week in 1..currentSchedule.scheduleEntity.recurrence!!.interval!!) {
                    val eventsForWeek = currentSchedule.events.filter { event ->
                        val rule = event.recurrenceRule ?: return@filter false
                        (rule.interval == 1 || event.periodNumber == week)
                    }
                    if (eventsForWeek.isNotEmpty()) {
                        this[week] = eventsForWeek
                            .groupBy { it.startDatetime!!.dayOfWeek }
                    }
                }
            }
        }

        private fun calculateEventsForList(
            periodicEventsForCalendar: Map<Int, Map<DayOfWeek, List<Event>>>?,
            nonPeriodicEvents: List<Event>?,
            today: LocalDate,
            scheduleEntity: ScheduleEntity
        ): List<Pair<LocalDate, List<Event>>> {
            var allEvents = listOf<Event>()
            if (periodicEventsForCalendar == null && nonPeriodicEvents == null) {
                return listOf()
            } else if (nonPeriodicEvents != null) {
                allEvents = nonPeriodicEvents
            } else if (periodicEventsForCalendar != null) {
                val startDate = maxOf(today, scheduleEntity.startDate)
                val periodicEvents = buildList {
                    val weeksNumbers = getWeeksNumbers(startDate, scheduleEntity)
                    weeksNumbers.forEachIndexed { index, week ->
                        val eventsInWeek = periodicEventsForCalendar[week]?.values.orEmpty().flatten()
                        val currentWeekStartDate = startDate.plusWeeks(index.toLong())
                        eventsInWeek.forEach { event ->
                            val eventStartDayOfWeek = event.startDatetime?.dayOfWeek?.value ?: return@forEach
                            val newEventDate = currentWeekStartDate.plusDays(eventStartDayOfWeek - 1L)
                            if (newEventDate.isAfter(scheduleEntity.endDate)) return@forEach
                            val newEvent = event.copy(
                                startDatetime = newEventDate.atTime(event.startDatetime.toLocalTime()),
                                endDatetime = newEventDate.atTime(event.endDatetime?.toLocalTime()),
                            )
                            add(newEvent)
                        }
                    }
                }
                allEvents = periodicEvents
            }

            return allEvents
                .filter { it.startDatetime?.toLocalDate()?.isAfter(today.minusDays(1)) == true }
                .sortedBy { it.startDatetime }
                .groupBy { it.startDatetime!!.toLocalDate() }
                .toList()
        }

        private fun getWeeksNumbers(
            startDate: LocalDate,
            scheduleEntity: ScheduleEntity
        ): List<Int> {
            val weeksCount = ChronoUnit.WEEKS.between(
                calculateFirstDayOfWeek(scheduleEntity.startDate),
                calculateFirstDayOfWeek(scheduleEntity.endDate)
            ).toInt() + 1
            val weeksRemaining =
                ChronoUnit.WEEKS.between(
                    calculateFirstDayOfWeek(startDate),
                    calculateFirstDayOfWeek(scheduleEntity.endDate)
                )
                    .toInt() + 1
            val recurrence = scheduleEntity.recurrence ?: return emptyList()

            return (1..weeksCount)
                .asSequence()
                .map { week -> ((week + recurrence.firstWeekNumber) % recurrence.interval!!).plus(1) }
                .toList()
                .drop((weeksCount - weeksRemaining))
        }
    }
}
