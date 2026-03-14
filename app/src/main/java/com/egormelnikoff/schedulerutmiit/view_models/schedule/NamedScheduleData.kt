package com.egormelnikoff.schedulerutmiit.view_models.schedule

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.extension.getCurrentWeek
import com.egormelnikoff.schedulerutmiit.app.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.app.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.app.widget.ui.EventsWidget.Companion.eveningTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@Keep
data class NamedScheduleData(
    val namedSchedule: NamedScheduleFormatted,
    val scheduleData: ScheduleData? = null
) {
    companion object {
        fun findCurrentSchedule(
            namedSchedule: NamedScheduleFormatted
        ): ScheduleFormatted? {
            return namedSchedule.schedules.find { it.scheduleEntity.isDefault }
                ?: namedSchedule.schedules.firstOrNull()
        }

        operator fun invoke(
            namedScheduleFormatted: NamedScheduleFormatted?
        ): NamedScheduleData? {
            namedScheduleFormatted ?: return null
            val currentSchedule = findCurrentSchedule(namedScheduleFormatted)
            currentSchedule ?: return NamedScheduleData(
                namedSchedule = namedScheduleFormatted
            )

            return NamedScheduleData(
                namedSchedule = namedScheduleFormatted,
                scheduleData = ScheduleData(
                    schedule = currentSchedule
                )
            )
        }
    }
}

@Keep
data class ScheduleData(
    val scheduleEntity: ScheduleEntity,
    val periodicEvents: Map<Int, Map<DayOfWeek, List<EventEntity>>>? = null,
    val nonPeriodicEvents: Map<LocalDate, List<EventEntity>>? = null,
    val fullEventList: List<Pair<LocalDate, List<EventEntity>>> = listOf(),
    val hiddenEvents: List<EventEntity> = listOf(),
    val eventsExtraData: List<EventExtraData> = listOf(),
    val schedulePagerData: SchedulePagerData,
    val reviewData: ReviewData
) {
    companion object {
        operator fun invoke(
            schedule: ScheduleFormatted
        ): ScheduleData {
            val today = LocalDateTime.now()
            val splitEvents = schedule.events.partition { it.isHidden }

            var periodicEventsForCalendar: Map<Int, Map<DayOfWeek, List<EventEntity>>>? = null
            var nonPeriodicEventsForCalendar: Map<LocalDate, List<EventEntity>>? = null

            schedule.scheduleEntity.recurrence?.let {
                periodicEventsForCalendar = splitEvents.second.getPeriodicEvents(
                    schedule.scheduleEntity.recurrence.interval
                )
            } ?: {
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

            val schedulePagerData = SchedulePagerData(
                today = today.toLocalDate(),
                startDate = schedule.scheduleEntity.startDate,
                endDate = schedule.scheduleEntity.endDate
            )

            val reviewData = ReviewData(
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
                hiddenEvents = splitEvents.first,
                reviewData = reviewData
            )
        }

        fun List<EventEntity>.getPeriodicEvents(
            interval: Int
        ): Map<Int, Map<DayOfWeek, List<EventEntity>>> {
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
            periodicEvents: Map<Int, Map<DayOfWeek, List<EventEntity>>>?,
            nonPeriodicEventsList: List<EventEntity>?
        ): List<Pair<LocalDate, List<EventEntity>>> {
            var fullEventsList = listOf<EventEntity>()
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
                ((week + recurrence.firstWeekNumber) % recurrence.interval)
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
        operator fun invoke(
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
    val events: Map<String, List<EventEntity>> = mapOf(),
    val countEventsForWeek: Int = 0
) {
    companion object {
        operator fun invoke(
            date: LocalDateTime,
            scheduleEntity: ScheduleEntity,
            periodicEvents: Map<Int, Map<DayOfWeek, List<EventEntity>>>?,
            nonPeriodicEvents: Map<LocalDate, List<EventEntity>>?
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
                events.values.flatten().last().endDatetime.toLocalTimeWithTimeZone()
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
            periodicEvents: Map<Int, Map<DayOfWeek, List<EventEntity>>>?,
            nonPeriodicEvents: Map<LocalDate, List<EventEntity>>?
        ): Int {
            if (scheduleEntity.startDate > date || date > scheduleEntity.endDate) return 0
            var eventsCountForWeek = 0
            when {
                (periodicEvents != null && scheduleEntity.recurrence != null) -> {
                    val currentWeek = date.getCurrentWeek(
                        startDate = scheduleEntity.startDate,
                        recurrence = scheduleEntity.recurrence
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
            nonPeriodicEvents: Map<LocalDate, List<EventEntity>>
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