package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.TimetableType
import com.egormelnikoff.schedulerutmiit.app.extension.getTimeSlotName
import com.egormelnikoff.schedulerutmiit.app.model.NonPeriodicContent
import com.egormelnikoff.schedulerutmiit.app.model.PeriodicContent
import com.egormelnikoff.schedulerutmiit.app.model.Schedule
import com.egormelnikoff.schedulerutmiit.app.model.Timetable
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import javax.inject.Inject

class ScheduleNormalizer @Inject constructor(
    private val scheduleRepos: ScheduleRepos
) {
    suspend operator fun invoke(
        schedule: Schedule,
        apiId: String,
        timetable: Timetable
    ): Schedule {
        val fixedSchedule = when (timetable.type) {
            TimetableType.NON_PERIODIC, TimetableType.SESSION -> {
                Schedule(
                    timetable = timetable,
                    periodicContent = null,
                    nonPeriodicContent = NonPeriodicContent(
                        events = schedule.nonPeriodicContent?.events
                            ?: schedule.periodicContent?.events
                    )
                )
            }

            TimetableType.PERIODIC -> {
                Schedule(
                    timetable = timetable,
                    nonPeriodicContent = null,
                    periodicContent = if (schedule.periodicContent != null) {
                        schedule.periodicContent
                    } else {
                        val clearedEvents = schedule.nonPeriodicContent?.events
                            ?.filter { it.startDatetime != null && it.endDatetime != null && it.name != null }
                            ?.distinctBy {
                                it.customHashCode()
                            }
                            ?: emptyList()

                        val eventsByWeek = clearedEvents.groupBy {
                            it.startDatetime!!.get(WeekFields.ISO.weekOfYear())
                        }
                        val weeksIndexes = eventsByWeek.keys.toList()
                        val checkedEvents = eventsByWeek.flatMap { (weekIndex, eventsInWeek) ->
                            eventsInWeek.map { event ->
                                event.copy(
                                    timeSlotName = getTimeSlotName(
                                        startDateTime = event.startDatetime!!,
                                        endDateTime = event.endDatetime!!
                                    ),
                                    periodNumber = (weekIndex % weeksIndexes.size + 1)
                                )
                            }
                        }
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                        val currentPeriodNumber = scheduleRepos.fetchCurrentWeek(
                            apiId,
                            startDate = formatter.format(timetable.startDate),
                            type = timetable.id.trim().last()
                        )

                        PeriodicContent(
                            events = checkedEvents,
                            recurrence = Recurrence(
                                currentNumber = currentPeriodNumber,
                                interval = 2,
                                firstWeekNumber = currentPeriodNumber
                            )
                        )
                    }
                )
            }
        }
        return fixedSchedule
    }
}