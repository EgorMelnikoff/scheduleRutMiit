package com.egormelnikoff.schedulerutmiit.app.widget

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.NamedScheduleData
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ReviewData
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleData.Companion.getPeriodicEvents
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

@Keep
data class WidgetData(
    val namedScheduleEntity: NamedScheduleEntity? = null,
    val settledScheduleEntity: ScheduleEntity? = null,
    val reviewData: ReviewData? = null,
    val eventsExtraData: List<EventExtraData> = listOf()
) {
    companion object {
        operator fun invoke(namedSchedule: NamedScheduleFormatted): WidgetData? {
            val scheduleFormatted = NamedScheduleData.findCurrentSchedule(namedSchedule)
            return if (scheduleFormatted != null) {
                val today = LocalDateTime.now()
                val splitEvents = scheduleFormatted.events.partition { it.isHidden }

                var periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>? = null
                var nonPeriodicEvents: Map<LocalDate, List<Event>>? = null

                if (scheduleFormatted.scheduleEntity.recurrence != null) {
                    periodicEvents = splitEvents.second.getPeriodicEvents(
                        scheduleFormatted.scheduleEntity.recurrence.interval,
                    )
                } else {
                    nonPeriodicEvents = splitEvents.second.groupBy {
                        it.startDatetime!!.toLocalDate()
                    }
                }
                val reviewData = ReviewData(
                    date = today,
                    scheduleEntity = scheduleFormatted.scheduleEntity,
                    periodicEvents = periodicEvents,
                    nonPeriodicEvents = nonPeriodicEvents
                )
                WidgetData(
                    namedScheduleEntity = namedSchedule.namedScheduleEntity,
                    settledScheduleEntity = scheduleFormatted.scheduleEntity,
                    eventsExtraData = scheduleFormatted.eventsExtraData,
                    reviewData = reviewData
                )
            } else null
        }
    }
}