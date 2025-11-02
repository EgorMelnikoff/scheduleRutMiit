package com.egormelnikoff.schedulerutmiit.app.widget

import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.calculateCurrentWeek
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleData
import java.time.DayOfWeek
import java.time.LocalDate

data class WidgetData(
    val namedSchedule: NamedScheduleFormatted? = null,
    val settledScheduleEntity: ScheduleEntity? = null,
    val periodicEventsForCalendar: Map<Int, Map<DayOfWeek, List<Event>>>? = null,
    val nonPeriodicEventsForCalendar: Map<LocalDate, List<Event>>? = null,
    val eventsExtraData: List<EventExtraData> = listOf(),
    var countEventsForWeek: Int = 0,
) {
    companion object {
        fun calculateWidgetData(namedSchedule: NamedScheduleFormatted): WidgetData? {
            val today = LocalDate.now()
            val scheduleFormatted = ScheduleData.findCurrentSchedule(namedSchedule)
            return if (scheduleFormatted != null) {
                val scheduleWithoutHiddenEvents = scheduleFormatted.copy(
                    events = scheduleFormatted.events.filter { !it.isHidden }
                )
                if (scheduleWithoutHiddenEvents.scheduleEntity.recurrence != null) {
                    val periodicEventsForCalendar =
                        ScheduleData.calculatePeriodicEventsForCalendar(
                            scheduleWithoutHiddenEvents
                        )
                    val currentWeek = calculateCurrentWeek(
                        date = today,
                        startDate = scheduleWithoutHiddenEvents.scheduleEntity.startDate,
                        firstPeriodNumber = scheduleWithoutHiddenEvents.scheduleEntity.recurrence.firstWeekNumber,
                        interval = scheduleWithoutHiddenEvents.scheduleEntity.recurrence.interval!!
                    )
                    val countEventsForWeek = periodicEventsForCalendar[currentWeek]!!
                        .flatMap { it.value }
                        .distinctBy { it.startDatetime }
                        .size

                    WidgetData(
                        namedSchedule = namedSchedule,
                        settledScheduleEntity = scheduleWithoutHiddenEvents.scheduleEntity,
                        periodicEventsForCalendar = periodicEventsForCalendar,
                        nonPeriodicEventsForCalendar = null,
                        countEventsForWeek = countEventsForWeek,
                        eventsExtraData = scheduleWithoutHiddenEvents.eventsExtraData
                    )
                } else {
                    val nonPeriodicEventsForCalendar = scheduleWithoutHiddenEvents.events
                        .groupBy { it.startDatetime!!.toLocalDate() }

                    val countEventsForWeek = ScheduleData.getEventsCountForWeek(
                        today = today,
                        events = nonPeriodicEventsForCalendar
                    )
                    WidgetData(
                        namedSchedule = namedSchedule,
                        settledScheduleEntity = scheduleWithoutHiddenEvents.scheduleEntity,
                        periodicEventsForCalendar = null,
                        nonPeriodicEventsForCalendar = nonPeriodicEventsForCalendar,
                        countEventsForWeek = countEventsForWeek,
                        eventsExtraData = scheduleWithoutHiddenEvents.eventsExtraData
                    )
                }
            } else null
        }
    }
}