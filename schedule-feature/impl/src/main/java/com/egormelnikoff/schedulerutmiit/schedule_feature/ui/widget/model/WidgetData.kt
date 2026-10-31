package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.widget.model

import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Event
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.utils.extension.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.SummaryState
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
                        s.schedule.recurrence!!.interval,
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