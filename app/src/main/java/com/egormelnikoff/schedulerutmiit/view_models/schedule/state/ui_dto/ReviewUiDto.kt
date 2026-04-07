package com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ui_dto

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.extension.getCurrentWeek
import com.egormelnikoff.schedulerutmiit.app.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.app.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.app.widget.ui.EventsWidget.Companion.eveningTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

@Keep
data class ReviewUiDto(
    val displayedDate: LocalDate,
    val events: Map<String, List<Event>> = mapOf(),
    val currentWeek: Int = 0
) {
    companion object {
        operator fun invoke(
            date: LocalDateTime,
            scheduleEntity: ScheduleEntity,
            periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
            nonPeriodicEvents: Map<LocalDate, List<Event>>?
        ): ReviewUiDto {
            var displayedDate = date.toLocalDate()
            var events = date.toLocalDate().getEventsForDate(
                scheduleEntity = scheduleEntity,
                periodicEvents = periodicEvents,
                nonPeriodicEvents = nonPeriodicEvents
            )

            var currentWeek = scheduleEntity.recurrence?.let {
                displayedDate.getCurrentWeek(
                    startDate = scheduleEntity.startDate,
                    recurrence = scheduleEntity.recurrence
                )
            } ?: 0

            val isFinishedEvents = events.isNotEmpty() && date.toLocalTime().isAfter(
                events.values.flatten().last().endDatetime.toLocalTimeWithTimeZone()
            )

            val nextDay = events.isEmpty() && date.toLocalTime().isAfter(eveningTime)

            if (isFinishedEvents || nextDay) {
                val tomorrow = date.plusDays(1)
                displayedDate = tomorrow.toLocalDate()
                events = displayedDate.getEventsForDate(
                    scheduleEntity = scheduleEntity,
                    periodicEvents = periodicEvents,
                    nonPeriodicEvents = nonPeriodicEvents
                )
                currentWeek = scheduleEntity.recurrence?.let {
                    displayedDate.getCurrentWeek(
                        startDate = scheduleEntity.startDate,
                        recurrence = scheduleEntity.recurrence
                    )
                } ?: 0
            }

            return ReviewUiDto(
                displayedDate = displayedDate,
                currentWeek = currentWeek,
                events = events
            )
        }
    }
}