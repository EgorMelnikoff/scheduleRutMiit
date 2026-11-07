package com.egormelnikoff.schedulerutmiit.feature_schedule.glance.model

import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.SummaryState
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate

@Serializable
data class WidgetData(
    val scheduleString: String? = null,
    val dateString: String? = null,
    val events: List<WidgetDataEventItem> = emptyList(),
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
                        s.schedule.recurrence.interval,
                    )
                } else {
                    nonPeriodicEvents = splitEvents.second.groupBy {
                        it.startDatetime.toLocalDate()
                    }
                }

                val summaryState = SummaryState(
                    schedule = s.schedule,
                    periodicEvents = periodicEvents,
                    nonPeriodicEvents = nonPeriodicEvents
                )

                val scheduleString = namedSchedule?.let {
                    buildString {
                        append(namedSchedule.shortName)
                        append(" (${s.schedule.timetableType.typeName})")
                    }
                }


                WidgetData(
                    scheduleString = scheduleString,
                )
            }
        }

    }
}