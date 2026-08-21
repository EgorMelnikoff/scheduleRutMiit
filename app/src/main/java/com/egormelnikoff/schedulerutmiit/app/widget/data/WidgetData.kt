package com.egormelnikoff.schedulerutmiit.app.widget.data

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.review.view_model.state.SummaryState
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate

@Serializable
data class WidgetData(
    val namedSchedule: NamedSchedule? = null,
    val settledSchedule: Schedule? = null,
    val summaryState: SummaryState? = null,
    val eventsExtraData: Map<Long, List<EventExtraData>> = mapOf(),
    val eventExtraPolicy: EventExtraPolicy = EventExtraPolicy.DEFAULT
) {
    companion object {
        operator fun invoke(
            namedSchedule: NamedSchedule?,
            scheduleWithEvents: ScheduleWithEvents?,
            eventExtraPolicy: EventExtraPolicy
        ): WidgetData? {
            return scheduleWithEvents?.let { s ->
                val splitEvents = s.events.partition { it.isHidden }

                var periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>? = null
                var nonPeriodicEvents: Map<LocalDate, List<Event>>? = null

                if (s.schedule.recurrence != null) {
                    periodicEvents = splitEvents.second.getPeriodicEvents(
                        s.schedule.recurrence!!.interval,
                    )
                } else {
                    nonPeriodicEvents = splitEvents.second.groupBy {
                        it.startDatetime.toLocalDate()
                    }
                }

                WidgetData(
                    namedSchedule = namedSchedule,
                    settledSchedule = s.schedule,
                    eventsExtraData = s.eventsExtraData.groupBy { it.eventId },
                    summaryState = SummaryState(
                        schedule = s.schedule,
                        periodicEvents = periodicEvents,
                        nonPeriodicEvents = nonPeriodicEvents
                    ),
                    eventExtraPolicy = eventExtraPolicy
                )
            }
        }

    }
}