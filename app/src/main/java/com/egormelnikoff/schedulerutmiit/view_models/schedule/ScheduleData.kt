package com.egormelnikoff.schedulerutmiit.view_models.schedule

import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.calculateCurrentWeek
import com.egormelnikoff.schedulerutmiit.app.model.calculateFirstDayOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

data class ScheduleData(
    val namedSchedule: NamedScheduleFormatted? = null,
    val settledScheduleEntity: ScheduleEntity? = null,

    val periodicEventsForCalendar: Map<Int, Map<DayOfWeek, List<Event>>>? = null,
    val nonPeriodicEventsForCalendar: Map<LocalDate, List<Event>>? = null,
    val eventForList: List<Pair<LocalDate, List<Event>>> = listOf(),
    val eventsExtraData: List<EventExtraData> = listOf(),
    val hiddenEvents: List<Event> = listOf(),
    var eventsForTomorrow: List<Event> = listOf(),
    var countEventsForWeek: Int = 0,

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
            return scheduleFormatted?.let { schedule ->
                val today = LocalDate.now()
                val weeksCount = ChronoUnit.WEEKS.between(
                    schedule.scheduleEntity.startDate.calculateFirstDayOfWeek(),
                    schedule.scheduleEntity.endDate.calculateFirstDayOfWeek()
                ).plus(1).toInt()

                val defaultParams = calculateDefaultParams(
                    today = today,
                    weeksCount = weeksCount,
                    scheduleEntity = schedule.scheduleEntity
                )
                val scheduleWithoutHiddenEvents = schedule.copy(
                    events = schedule.events.filter { !it.isHidden }
                )
                val hiddenEvents = schedule.events.filter { it.isHidden }

                if (scheduleWithoutHiddenEvents.scheduleEntity.recurrence != null) {
                    val periodicEventsForCalendar =
                        calculatePeriodicEventsForCalendar(scheduleWithoutHiddenEvents)
                    val eventsForList = calculateEventsForList(
                        today = today,
                        periodicEventsForCalendar = periodicEventsForCalendar,
                        nonPeriodicEvents = null,
                        scheduleEntity = scheduleWithoutHiddenEvents.scheduleEntity,
                    )
                    val reviewParams = calculateReviewParams(
                        today = today,
                        periodicEventsForCalendar = periodicEventsForCalendar,
                        nonPeriodicEventsForCalendar = null,
                        scheduleEntity = schedule.scheduleEntity
                    )
                    ScheduleData(
                        namedSchedule = namedSchedule,
                        settledScheduleEntity = schedule.scheduleEntity,
                        periodicEventsForCalendar = periodicEventsForCalendar,
                        nonPeriodicEventsForCalendar = null,
                        eventsExtraData = scheduleWithoutHiddenEvents.eventsExtraData,
                        eventForList = eventsForList,
                        hiddenEvents = hiddenEvents,
                        eventsForTomorrow = reviewParams.first,
                        countEventsForWeek = reviewParams.second,
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
                        nonPeriodicEvents = schedule.events,
                        scheduleEntity = scheduleWithoutHiddenEvents.scheduleEntity,
                    )
                    val reviewParams = calculateReviewParams(
                        today = today,
                        periodicEventsForCalendar = null,
                        nonPeriodicEventsForCalendar = nonPeriodicEventsForCalendar,
                        scheduleEntity = schedule.scheduleEntity
                    )
                    ScheduleData(
                        namedSchedule = namedSchedule,
                        settledScheduleEntity = schedule.scheduleEntity,
                        periodicEventsForCalendar = null,
                        nonPeriodicEventsForCalendar = nonPeriodicEventsForCalendar,
                        eventsExtraData = scheduleWithoutHiddenEvents.eventsExtraData,
                        eventForList = eventsForList,
                        hiddenEvents = hiddenEvents,
                        eventsForTomorrow = reviewParams.first,
                        countEventsForWeek = reviewParams.second,
                        weeksCount = weeksCount,
                        defaultDate = defaultParams.first,
                        weeksStartIndex = defaultParams.second,
                        daysStartIndex = defaultParams.third
                    )
                }
            }
        }

        fun findCurrentSchedule(
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
                        scheduleEntity.startDate.calculateFirstDayOfWeek(),
                        today.calculateFirstDayOfWeek()
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

        fun calculatePeriodicEventsForCalendar(
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
                scheduleEntity.startDate.calculateFirstDayOfWeek(),
                scheduleEntity.endDate.calculateFirstDayOfWeek()
            ).toInt() + 1
            val weeksRemaining =
                ChronoUnit.WEEKS.between(
                    startDate.calculateFirstDayOfWeek(),
                    scheduleEntity.endDate.calculateFirstDayOfWeek()
                )
                    .toInt() + 1
            val recurrence = scheduleEntity.recurrence ?: return emptyList()

            return (1..weeksCount)
                .asSequence()
                .map { week -> ((week + recurrence.firstWeekNumber) % recurrence.interval!!).plus(1) }
                .toList()
                .drop((weeksCount - weeksRemaining))
        }

        private fun calculateReviewParams (
            today: LocalDate,
            periodicEventsForCalendar: Map<Int, Map<DayOfWeek, List<Event>>>?,
            nonPeriodicEventsForCalendar: Map<LocalDate, List<Event>>?,
            scheduleEntity: ScheduleEntity
        ): Pair<List<Event>, Int> {
            val displayedDate = today.plusDays(1)
            var eventsForTomorrow = listOf<Event>()
            var countEventsForWeek = 0

            periodicEventsForCalendar?.let { periodicEvents ->
                val recurrence = scheduleEntity.recurrence!!
                val startDate = scheduleEntity.startDate

                val currentWeek = calculateCurrentWeek(
                    date = displayedDate,
                    startDate = startDate,
                    interval = recurrence.interval!!,
                    firstPeriodNumber = recurrence.firstWeekNumber
                )

                val eventsForCurrentWeek = periodicEvents[currentWeek] ?: emptyMap()
                val eventsForTomorrowPeriodic =
                    eventsForCurrentWeek[displayedDate.dayOfWeek]

                eventsForTomorrow = eventsForTomorrowPeriodic
                    ?.distinctBy { it.startDatetime }
                    ?: listOf()

                countEventsForWeek = eventsForCurrentWeek
                    .flatMap { it.value }
                    .distinctBy { it.startDatetime }
                    .size

            } ?: nonPeriodicEventsForCalendar?.let { nonPeriodicEvents ->
                val eventsForTomorrowNonPeriodic = nonPeriodicEvents[displayedDate]
                eventsForTomorrow = eventsForTomorrowNonPeriodic ?: listOf()
                countEventsForWeek = getEventsCountForWeek(displayedDate, nonPeriodicEvents)
            }
            return Pair(eventsForTomorrow, countEventsForWeek)
        }

        fun getEventsCountForWeek(
            today: LocalDate,
            events: Map<LocalDate, List<Event>>
        ): Int {
            var count = 0
            val firstDayOfWeek = today.calculateFirstDayOfWeek()
            for (date in 0 until 7) {
                val currentDate = firstDayOfWeek.plusDays(date.toLong())
                val eventPerDay = events[currentDate]?.distinctBy { it.startDatetime }
                count += eventPerDay?.size ?: 0
            }
            return count
        }
    }
}
