package com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateSerializer
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getCurrentWeek
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getEventsForDate
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

val eveningTime: LocalTime = LocalTime.of(18, 0)

@Serializable
data class ReviewState(
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val events: Map<String, List<Event>> = mapOf(),
    val currentWeek: Int = -1
) {
    companion object {
        operator fun invoke(
            schedule: Schedule,
            periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
            nonPeriodicEvents: Map<LocalDate, List<Event>>?
        ): ReviewState {
            val dateTime = LocalDateTime.now()
            var date = dateTime.toLocalDate()
            var events = schedule.getEventsForDate(
                date = date,
                periodicEvents = periodicEvents,
                nonPeriodicEvents = nonPeriodicEvents
            )
            var currentWeek = date.getCurrentWeek(
                startDate = schedule.startDate,
                recurrence = schedule.recurrence
            )

            val isFinishedEvents = events.isNotEmpty() && dateTime.toLocalTime().isAfter(
                events.values.flatten().last().endDatetime.toLocalTimeWithTimeZone()
            )

            val nextDay = events.isEmpty() && dateTime.toLocalTime().isAfter(eveningTime)

            if (isFinishedEvents || nextDay) {
                date = date.plusDays(1)
                events = schedule.getEventsForDate(
                    date = date,
                    periodicEvents = periodicEvents,
                    nonPeriodicEvents = nonPeriodicEvents
                )
                currentWeek = date.getCurrentWeek(
                    startDate = schedule.startDate,
                    recurrence = schedule.recurrence
                )
            }

            return ReviewState(
                date = date,
                currentWeek = currentWeek,
                events = events
            )
        }
    }
}