package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.extension.getEventsForDate
import com.egormelnikoff.schedulerutmiit.core.common.extension.getCurrentWeek
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateSerializer
import com.egormelnikoff.schedulerutmiit.core.database.entity.Event
import com.egormelnikoff.schedulerutmiit.core.database.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.widget.ui.EventsWidget.Companion.eveningTime
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

@Keep
@Serializable
data class ReviewUiDto(
    @Serializable(with = LocalDateSerializer::class)
    val displayedDate: LocalDate,
    val events: Map<String, List<Event>> = mapOf(),
    val currentWeek: Int = 0
) {
    companion object {
        operator fun invoke(
            scheduleEntity: ScheduleEntity,
            periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
            nonPeriodicEvents: Map<LocalDate, List<Event>>?
        ): ReviewUiDto {
            val date = LocalDateTime.now()
            var displayedDate = date.toLocalDate()
            var events = scheduleEntity.getEventsForDate(
                date = displayedDate,
                periodicEvents = periodicEvents,
                nonPeriodicEvents = nonPeriodicEvents
            )
            var currentWeek = displayedDate.getCurrentWeek(
                startDate = scheduleEntity.startDate,
                recurrence = scheduleEntity.recurrence
            )

            val isFinishedEvents = events.isNotEmpty() && date.toLocalTime().isAfter(
                events.values.flatten().last().endDatetime.toLocalTimeWithTimeZone()
            )

            val nextDay = events.isEmpty() && date.toLocalTime().isAfter(eveningTime)

            if (isFinishedEvents || nextDay) {
                displayedDate = displayedDate.plusDays(1)
                events = scheduleEntity.getEventsForDate(
                    date = displayedDate,
                    periodicEvents = periodicEvents,
                    nonPeriodicEvents = nonPeriodicEvents
                )
                currentWeek = displayedDate.getCurrentWeek(
                    startDate = scheduleEntity.startDate,
                    recurrence = scheduleEntity.recurrence
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