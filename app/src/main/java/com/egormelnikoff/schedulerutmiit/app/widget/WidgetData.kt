package com.egormelnikoff.schedulerutmiit.app.widget

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ui_dto.NamedScheduleUiDto
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ui_dto.ReviewUiDto
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ui_dto.ScheduleUiDto.Companion.getPeriodicEvents
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

@Keep
data class WidgetData(
    val namedScheduleEntity: NamedScheduleEntity? = null,
    val settledScheduleEntity: ScheduleEntity? = null,
    val reviewUiDto: ReviewUiDto? = null,
    val eventsExtraData: List<EventExtraData> = listOf()
) {
    companion object {
        operator fun invoke(namedSchedule: NamedSchedule): WidgetData? {
            val scheduleFormatted = NamedScheduleUiDto.findCurrentSchedule(namedSchedule)
            return if (scheduleFormatted != null) {
                val splitEvents = scheduleFormatted.events.partition { it.isHidden }

                var periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>? = null
                var nonPeriodicEvents: Map<LocalDate, List<Event>>? = null

                if (scheduleFormatted.scheduleEntity.recurrence != null) {
                    periodicEvents = splitEvents.second.getPeriodicEvents(
                        scheduleFormatted.scheduleEntity.recurrence.interval,
                    )
                } else {
                    nonPeriodicEvents = splitEvents.second.groupBy {
                        it.startDatetime.toLocalDate()
                    }
                }
                WidgetData(
                    namedScheduleEntity = namedSchedule.namedScheduleEntity,
                    settledScheduleEntity = scheduleFormatted.scheduleEntity,
                    eventsExtraData = scheduleFormatted.eventsExtraData,
                    reviewUiDto = ReviewUiDto(
                        date = LocalDateTime.now(),
                        scheduleEntity = scheduleFormatted.scheduleEntity,
                        periodicEvents = periodicEvents,
                        nonPeriodicEvents = nonPeriodicEvents
                    )
                )
            } else null
        }
    }
}