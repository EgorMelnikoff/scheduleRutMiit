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
data class ReviewUiDto(
    @Serializable(with = LocalDateSerializer::class)
    val displayedDate: LocalDate,
    val events: Map<String, List<Event>> = mapOf(),
    val currentWeek: Int = 0
) {
    companion object {
        operator fun invoke(
            schedule: Schedule,
            periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
            nonPeriodicEvents: Map<LocalDate, List<Event>>?
        ): ReviewUiDto {
            val date = LocalDateTime.now()
            var displayedDate = date.toLocalDate()
            var events = schedule.getEventsForDate(
                date = displayedDate,
                periodicEvents = periodicEvents,
                nonPeriodicEvents = nonPeriodicEvents
            )
            var currentWeek = displayedDate.getCurrentWeek(
                startDate = schedule.startDate,
                recurrence = schedule.recurrence
            )

            val isFinishedEvents = events.isNotEmpty() && date.toLocalTime().isAfter(
                events.values.flatten().last().endDatetime.toLocalTimeWithTimeZone()
            )

            val nextDay = events.isEmpty() && date.toLocalTime().isAfter(eveningTime)

            if (isFinishedEvents || nextDay) {
                displayedDate = displayedDate.plusDays(1)
                events = schedule.getEventsForDate(
                    date = displayedDate,
                    periodicEvents = periodicEvents,
                    nonPeriodicEvents = nonPeriodicEvents
                )
                currentWeek = displayedDate.getCurrentWeek(
                    startDate = schedule.startDate,
                    recurrence = schedule.recurrence
                )
            }

            return ReviewUiDto(
                displayedDate = displayedDate,
                currentWeek = currentWeek,
                events = events
            )
        }
    }
}