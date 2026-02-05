package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.app.model.Schedule
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.abs

class ScheduleMapper @Inject constructor() {
    operator fun invoke(
        schedule: Schedule,
        primaryKeyNamedSchedule: Long,
        index: Int
    ): ScheduleFormatted? {
        val events = mutableListOf<Event>()
        schedule.periodicContent?.events
            ?.filter { it.startDatetime != null }
            ?.let { events.addAll(it) }
        schedule.nonPeriodicContent?.events
            ?.filter { it.startDatetime != null }
            ?.let { events.addAll(it) }

        if (events.isNotEmpty()) {
            val today = LocalDate.now()
            val scheduleEntity = ScheduleEntity(
                namedScheduleId = primaryKeyNamedSchedule,
                startDate = schedule.timetable.startDate,
                endDate = schedule.timetable.endDate,
                recurrence = schedule.periodicContent?.let {
                    getRecurrence(
                        today,
                        schedule.timetable.startDate,
                        schedule.periodicContent.recurrence
                    )
                },
                timetableType = schedule.timetable.type,
                downloadUrl = schedule.timetable.downloadUrl,
                timetableId = schedule.timetable.id,
                isDefault = index == 0
            )
            return ScheduleFormatted(
                scheduleEntity = scheduleEntity,
                events = events,
                eventsExtraData = mutableListOf()
            )
        }
        return null
    }

    private fun getRecurrence(
        today: LocalDate,
        startDate: LocalDate,
        recurrence: Recurrence
    ): Recurrence {
        return if (today > startDate) {
            val currentWeekIndex = abs(
                ChronoUnit.WEEKS.between(
                    startDate.getFirstDayOfWeek(),
                    today.getFirstDayOfWeek()
                )
            ).plus(1)

            val firstWeekNumber =
                (currentWeekIndex + recurrence.currentNumber) % recurrence.interval
                    .plus(1)

            recurrence.copy(
                firstWeekNumber = firstWeekNumber.toInt()
            )
        } else {
            recurrence.copy(
                firstWeekNumber = recurrence.currentNumber
            )
        }
    }
}