package com.egormelnikoff.schedulerutmiit.app.widget

import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData.Companion.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ReviewData
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

data class WidgetData(
    val namedSchedule: NamedScheduleFormatted? = null,
    val settledScheduleEntity: ScheduleEntity? = null,
    val reviewData: ReviewData,
    val eventsExtraData: List<EventExtraData> = listOf(),
) {
    companion object {
        fun getWidgetData(namedSchedule: NamedScheduleFormatted): WidgetData? {
            val scheduleFormatted = NamedScheduleData.findCurrentSchedule(namedSchedule)
            return if (scheduleFormatted != null) {
                val today = LocalDateTime.now()
                val splitEvents = scheduleFormatted.events.partition { it.isHidden }

                var periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>? = null
                var nonPeriodicEvents: Map<LocalDate, List<Event>>? = null

                if (scheduleFormatted.scheduleEntity.recurrence != null) {
                    periodicEvents = splitEvents.second.getPeriodicEvents(
                        scheduleFormatted.scheduleEntity.recurrence.interval!!,
                    )
                } else {
                    nonPeriodicEvents = splitEvents.second.groupBy {
                        it.startDatetime!!.toLocalDate()
                    }
                }
                val reviewData = ReviewData.getReviewData(
                    date = today,
                    scheduleEntity = scheduleFormatted.scheduleEntity,
                    periodicEvents = periodicEvents,
                    nonPeriodicEvents = nonPeriodicEvents
                )
                WidgetData(
                    namedSchedule = namedSchedule,
                    settledScheduleEntity = scheduleFormatted.scheduleEntity,
                    eventsExtraData = scheduleFormatted.eventsExtraData,
                    reviewData = reviewData
                )
            } else null
        }
    }
}