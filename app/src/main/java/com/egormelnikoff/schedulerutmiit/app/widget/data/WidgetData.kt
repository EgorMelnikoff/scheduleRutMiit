package com.egormelnikoff.schedulerutmiit.app.widget.data

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ui_dto.ReviewUiDto
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate

@Serializable
data class WidgetData(
    val namedSchedule: NamedSchedule? = null,
    val settledSchedule: Schedule? = null,
    val reviewUiDto: ReviewUiDto? = null,
    val eventsExtraData: Map<Long, EventExtraData> = mapOf(),
    val eventExtraPolicy: EventExtraPolicy = EventExtraPolicy.DEFAULT
) {
    companion object {
        operator fun invoke(
            namedSchedule: NamedSchedule?,
            scheduleWithEvents: ScheduleWithEvents?,
            eventExtraPolicy: EventExtraPolicy
        ): WidgetData? {
            return if (scheduleWithEvents != null) {
                val splitEvents = scheduleWithEvents.events.partition { it.isHidden }

                var periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>? = null
                var nonPeriodicEvents: Map<LocalDate, List<Event>>? = null

                if (scheduleWithEvents.schedule.recurrence != null) {
                    periodicEvents = splitEvents.second.getPeriodicEvents(
                        requireNotNull(scheduleWithEvents.schedule.recurrence).interval,
                    )
                } else {
                    nonPeriodicEvents = splitEvents.second.groupBy {
                        it.startDatetime.toLocalDate()
                    }
                }
                WidgetData(
                    namedSchedule = namedSchedule,
                    settledSchedule = scheduleWithEvents.schedule,
                    eventsExtraData = scheduleWithEvents.eventsExtraData.associateBy { it.eventId },
                    reviewUiDto = ReviewUiDto.Companion(
                        schedule = scheduleWithEvents.schedule,
                        periodicEvents = periodicEvents,
                        nonPeriodicEvents = nonPeriodicEvents
                    ),
                    eventExtraPolicy = eventExtraPolicy
                )
            } else null
        }
    }
}