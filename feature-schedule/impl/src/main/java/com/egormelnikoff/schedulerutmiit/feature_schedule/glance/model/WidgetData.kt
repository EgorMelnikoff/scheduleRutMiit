package com.egormelnikoff.schedulerutmiit.feature_schedule.glance.model

import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.findEventExtra
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.SummaryState
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate

@Serializable
data class WidgetData(
    val scheduleString: String? = null,
    val dateString: String? = null,
    val currentWeek: Int = -1,
    val events: Map<String, List<WidgetDataEventItem>> = emptyMap(),
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

                val today = LocalDate.now()

                val scheduleString = namedSchedule?.let {
                    buildString {
                        append(namedSchedule.shortName)
                        append(" (${s.schedule.timetableType.typeName})")
                    }
                }


                val dateString = when (summaryState.date) {
                    today -> {
                        "Сегодня, " + //${glanceStringResource(R.string.today)}
                                "${summaryState.date.format(dayMonthNameFormatter)}"
                    }

                    today.plusDays(1) -> {
                        "Завтра, " + //${glanceStringResource(R.string.tomorrow)}
                                "${summaryState.date.format(dayMonthNameFormatter)}"
                    }

                    else -> {
                        summaryState.date.format(dayMonthNameFormatter)
                    }
                }

                val eventsExtraData = scheduleWithEvents.eventsExtraData.groupBy { it.eventId }

                WidgetData(
                    scheduleString = scheduleString,
                    dateString = dateString,
                    currentWeek = summaryState.currentWeek,
                    events = summaryState.events.mapValues { entry ->
                        entry.value.map {
                            val eventExtraData = eventsExtraData.findEventExtra(
                                eventId = it.id,
                                date = summaryState.date,
                                eventExtraPolicy = eventExtraPolicy
                            )
                            WidgetDataEventItem(
                                name = it.name,
                                type = it.typeName,
                                startDateTime = it.startDatetime,
                                endDateTime = it.endDatetime,
                                rooms = it.rooms?.joinToString { it.name },
                                comment = eventExtraData?.comment,
                                tag = eventExtraData?.tag
                            )
                        }
                    }
                )
            }
        }

    }
}