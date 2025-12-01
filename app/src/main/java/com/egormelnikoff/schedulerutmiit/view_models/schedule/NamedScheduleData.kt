package com.egormelnikoff.schedulerutmiit.view_models.schedule

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.getCurrentWeek
import com.egormelnikoff.schedulerutmiit.app.model.getEventsForDate
import com.egormelnikoff.schedulerutmiit.app.model.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.app.model.toLocaleTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.app.widget.ui.EventsWidget.Companion.eveningTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@Keep
data class NamedScheduleData(
    val namedSchedule: NamedScheduleFormatted? = null,
    val scheduleData: ScheduleData? = null
) {
    companion object {
        fun findCurrentSchedule(
            namedSchedule: NamedScheduleFormatted
        ): ScheduleFormatted? {
            return namedSchedule.schedules.find { it.scheduleEntity.isDefault }
                ?: namedSchedule.schedules.firstOrNull()
        }

        fun getNamedScheduleData(
            namedSchedule: NamedScheduleFormatted?
        ): NamedScheduleData? {
            namedSchedule ?: return null
            val currentSchedule = findCurrentSchedule(namedSchedule)
            currentSchedule ?: return NamedScheduleData(
                namedSchedule = namedSchedule
            )
            val today = LocalDateTime.now()
            val scheduleData = ScheduleData.getScheduleData(
                today = today,
                schedule = currentSchedule
            )

            return NamedScheduleData(
                namedSchedule = namedSchedule,
                scheduleData = scheduleData
            )
        }
    }
}

@Keep
data class ScheduleData(
    val scheduleEntity: ScheduleEntity? = null,
    val periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>? = null,
    val nonPeriodicEvents: Map<LocalDate, List<Event>>? = null,
    val fullEventList: List<Pair<LocalDate, List<Event>>> = listOf(),
    val hiddenEvents: List<Event> = listOf(),
    val eventsExtraData: List<EventExtraData> = listOf(),
    val schedulePagerData: SchedulePagerData? = null,
    val reviewData: ReviewData? = null
) {
    companion object {
        fun getScheduleData(
            today: LocalDateTime,
            schedule: ScheduleFormatted
        ): ScheduleData {
            val splitEvents = schedule.events.partition { it.isHidden }
            val hiddenEvents = splitEvents.first
            val visibleEvents = splitEvents.second

            var periodicEventsForCalendar: Map<Int, Map<DayOfWeek, List<Event>>>? = null
            var nonPeriodicEventsForCalendar: Map<LocalDate, List<Event>>? = null

            if (schedule.scheduleEntity.recurrence != null) {
                periodicEventsForCalendar = visibleEvents
                    .getPeriodicEvents(
                        schedule.scheduleEntity.recurrence.interval!!
                    )
            } else {
                nonPeriodicEventsForCalendar = visibleEvents
                    .groupBy {
                        it.startDatetime!!.toLocalDate()
                    }
            }

            val fullEventList = getFullEventsList(
                today = today.toLocalDate(),
                periodicEvents = periodicEventsForCalendar,
                nonPeriodicEventsList = visibleEvents,
                scheduleEntity = schedule.scheduleEntity,
            )

            val schedulePagerData = SchedulePagerData.getSchedulePagerData(
                today = today.toLocalDate(),
                startDate = schedule.scheduleEntity.startDate,
                endDate = schedule.scheduleEntity.endDate
            )

            val reviewData = ReviewData.getReviewData(
                date = today,
                scheduleEntity = schedule.scheduleEntity,
                periodicEvents = periodicEventsForCalendar,
                nonPeriodicEvents = nonPeriodicEventsForCalendar
            )

            return ScheduleData(
                scheduleEntity = schedule.scheduleEntity,

                periodicEvents = periodicEventsForCalendar,
                schedulePagerData = schedulePagerData,
                nonPeriodicEvents = nonPeriodicEventsForCalendar,
                fullEventList = fullEventList,

                eventsExtraData = schedule.eventsExtraData,
                hiddenEvents = hiddenEvents,
                reviewData = reviewData
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
                        this[week] = eventsForWeek.groupBy { it.startDatetime!!.dayOfWeek }
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
                (periodicEvents != null) -> {
                    val currentStartDate = maxOf(today, scheduleEntity.startDate)
                    val events = buildList {
                        val weeksNumbers = getWeekNumbers(
                            currentStartDate = currentStartDate,
                            scheduleEntity = scheduleEntity
                        )
                        weeksNumbers.forEachIndexed { index, week ->
                            val eventsInWeek = periodicEvents[week]?.values.orEmpty().flatten()
                            val currentWeekStartDate = currentStartDate.plusWeeks(index.toLong())
                            eventsInWeek.forEach { event ->
                                val daysToAdd =
                                    event.startDatetime!!.dayOfWeek.value - today.dayOfWeek.value
                                val newEventDate = currentWeekStartDate.plusDays(daysToAdd.toLong())
                                val newEvent = event.copy(
                                    startDatetime = newEventDate.atTime(event.startDatetime.toLocalTime()),
                                    endDatetime = newEventDate.atTime(event.endDatetime?.toLocalTime()),
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
                .filter { it.startDatetime?.toLocalDate()?.isAfter(today.minusDays(1)) == true }
                .sortedBy { it.startDatetime }
                .groupBy { it.startDatetime!!.toLocalDate() }
                .toList()
        }

        private fun getWeekNumbers(
            currentStartDate: LocalDate,
            scheduleEntity: ScheduleEntity
        ): List<Int> {
            val weeksCount = ChronoUnit.WEEKS.between(
                scheduleEntity.startDate.getFirstDayOfWeek(),
                scheduleEntity.endDate.getFirstDayOfWeek()
            ).toInt() + 1
            val weeksRemaining = ChronoUnit.WEEKS.between(
                currentStartDate.getFirstDayOfWeek(),
                scheduleEntity.endDate.getFirstDayOfWeek()
            ).toInt()

            return (weeksCount - weeksRemaining..weeksCount).map { week ->
                ((week + scheduleEntity.recurrence!!.firstWeekNumber) % scheduleEntity.recurrence.interval!!)
                    .plus(1)
            }
        }
    }
}

@Keep
data class SchedulePagerData(
    val today: LocalDate,
    val defaultDate: LocalDate,
    val weeksCount: Int,
    val weeksStartIndex: Int,
    val daysCount: Int,
    val daysStartIndex: Int,
) {
    companion object {
        fun getSchedulePagerData(
            today: LocalDate,
            startDate: LocalDate,
            endDate: LocalDate
        ): SchedulePagerData {
            val weeksCount = ChronoUnit.WEEKS.between(
                startDate.getFirstDayOfWeek(),
                endDate.getFirstDayOfWeek()
            ).plus(1).toInt()

            val daysCount = ChronoUnit.DAYS.between(
                startDate,
                endDate
            ).plus(1).toInt()

            val defaultDate: LocalDate
            val weeksStartIndex: Int
            val daysStartIndex: Int

            if (today in startDate..endDate) {
                weeksStartIndex = abs(
                    ChronoUnit.WEEKS.between(
                        startDate.getFirstDayOfWeek(),
                        today.getFirstDayOfWeek()
                    ).toInt()
                )
                daysStartIndex = abs(
                    ChronoUnit.DAYS.between(
                        startDate,
                        today
                    ).toInt()
                )
                defaultDate = today
            } else if (today < startDate) {
                weeksStartIndex = 0
                daysStartIndex = 0
                defaultDate = startDate
            } else {
                weeksStartIndex = weeksCount
                daysStartIndex = weeksCount * 7
                defaultDate = endDate
            }
            return SchedulePagerData(
                today = today,
                defaultDate = defaultDate,
                weeksCount = weeksCount,
                weeksStartIndex = weeksStartIndex,
                daysCount = daysCount,
                daysStartIndex = daysStartIndex
            )
        }
    }
}

@Keep
data class ReviewData(
    val displayedDate: LocalDate,
    val events: Map<String, List<Event>> = mapOf(),
    val countEventsForWeek: Int = 0
) {
    companion object {
        fun getReviewData(
            date: LocalDateTime,
            scheduleEntity: ScheduleEntity,
            periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
            nonPeriodicEvents: Map<LocalDate, List<Event>>?
        ): ReviewData {
            var displayedDate = date.toLocalDate()
            var events = date.toLocalDate().getEventsForDate(
                scheduleEntity = scheduleEntity,
                periodicEvents = periodicEvents,
                nonPeriodicEvents = nonPeriodicEvents
            )
            var eventsCountForWeek = getEventCountForWeek(
                scheduleEntity = scheduleEntity,
                date = date.toLocalDate(),
                periodicEvents = periodicEvents,
                nonPeriodicEvents = nonPeriodicEvents
            )

            val isFinishedEvents = events.isNotEmpty() && date.toLocalTime().isAfter(
                events.values.flatten().last().endDatetime!!.toLocaleTimeWithTimeZone()
            )

            val nextDay = events.isEmpty() && date.toLocalTime().isAfter(eveningTime)

            if (isFinishedEvents || nextDay) {
                val tomorrow = date.plusDays(1)
                displayedDate = tomorrow.toLocalDate()
                events = tomorrow.toLocalDate().getEventsForDate(
                    scheduleEntity = scheduleEntity,
                    periodicEvents = periodicEvents,
                    nonPeriodicEvents = nonPeriodicEvents
                )
                eventsCountForWeek = getEventCountForWeek(
                    scheduleEntity = scheduleEntity,
                    date = tomorrow.toLocalDate(),
                    periodicEvents = periodicEvents,
                    nonPeriodicEvents = nonPeriodicEvents
                )
            }

            return ReviewData(
                displayedDate = displayedDate,
                countEventsForWeek = eventsCountForWeek,
                events = events
            )
        }

        private fun getEventCountForWeek(
            date: LocalDate,
            scheduleEntity: ScheduleEntity,
            periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
            nonPeriodicEvents: Map<LocalDate, List<Event>>?
        ): Int {
            var eventsCountForWeek = 0
            when {
                (periodicEvents != null) -> {
                    val currentWeek = getCurrentWeek(
                        date = date,
                        startDate = scheduleEntity.startDate,
                        recurrence = scheduleEntity.recurrence!!
                    )
                    eventsCountForWeek = periodicEvents[currentWeek]?.values?.flatten()?.size ?: 0
                }

                (nonPeriodicEvents != null) -> {
                    eventsCountForWeek = getNonPeriodicEventsCountForWeek(
                        date = date,
                        nonPeriodicEvents = nonPeriodicEvents
                    )
                }
            }
            return eventsCountForWeek
        }

        private fun getNonPeriodicEventsCountForWeek(
            date: LocalDate,
            nonPeriodicEvents: Map<LocalDate, List<Event>>
        ): Int {
            var count = 0
            val firstDayOfWeek = date.getFirstDayOfWeek()
            for (date in 0 until 7) {
                val currentDate = firstDayOfWeek.plusDays(date.toLong())
                val eventPerDay = nonPeriodicEvents[currentDate]?.distinctBy { it.startDatetime }
                count += eventPerDay?.size ?: 0
            }
            return count
        }
    }
}