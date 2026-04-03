package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.DateTimeFormatters.yearDateMonthFormatter
import com.egormelnikoff.schedulerutmiit.app.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.app.extension.getTimeSlotName
import com.egormelnikoff.schedulerutmiit.app.network.model.NonPeriodicContentModel
import com.egormelnikoff.schedulerutmiit.app.network.model.PeriodicContentModel
import com.egormelnikoff.schedulerutmiit.app.network.model.ScheduleModel
import com.egormelnikoff.schedulerutmiit.datasource.remote.schedule.ScheduleRemoteDataSource
import java.time.temporal.WeekFields
import javax.inject.Inject

class ScheduleApiNormalizer @Inject constructor(
    private val scheduleRemoteDataSource: ScheduleRemoteDataSource
) {
    suspend operator fun invoke(
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        schedule: ScheduleModel
    ): ScheduleModel {
        val fixedSchedule = when (schedule.timetable.type) {
            TimetableType.NON_PERIODIC, TimetableType.SESSION -> {
                ScheduleModel(
                    timetable = schedule.timetable,
                    periodicContent = null,
                    nonPeriodicContent = NonPeriodicContentModel(
                        events = schedule.nonPeriodicContent?.events
                            ?: schedule.periodicContent?.events
                    )
                )
            }

            TimetableType.PERIODIC -> {
                ScheduleModel(
                    timetable = schedule.timetable,
                    nonPeriodicContent = null,
                    periodicContent = if (schedule.periodicContent != null) {
                        schedule.periodicContent
                    } else {
                        val clearedEvents = schedule.nonPeriodicContent?.events
                            ?.filter { it.startDatetime != null && it.endDatetime != null && it.name != null }
                            ?.distinctBy {
                                it.customHashCode()
                            } ?: emptyList()

                        val eventsByWeek = clearedEvents.groupBy {
                            it.startDatetime!!.get(WeekFields.ISO.weekOfYear())
                        }

                        val checkedEvents = eventsByWeek.flatMap { (weekIndex, eventsInWeek) ->
                            eventsInWeek.map { event ->
                                event.copy(
                                    timeSlotName = getTimeSlotName(
                                        startDateTime = event.startDatetime,
                                        endDateTime = event.endDatetime
                                    ),
                                    periodNumber = (weekIndex % eventsByWeek.keys.size + 1)
                                )
                            }
                        }

                        val currentPeriodNumber = scheduleRemoteDataSource.fetchCurrentWeek(
                            namedScheduleType,
                            apiId,
                            startDate = schedule.timetable.startDate.format(yearDateMonthFormatter),
                            type = schedule.timetable.id.trim()
                        )

                        PeriodicContentModel(
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