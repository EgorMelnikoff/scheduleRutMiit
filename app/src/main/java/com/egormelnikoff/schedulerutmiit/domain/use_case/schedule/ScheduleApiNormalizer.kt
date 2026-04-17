package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.app.DateTimeFormatters.yearDateMonthFormatter
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.app.extension.getTimeSlotName
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.NonPeriodicContentDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.PeriodicContentDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.ScheduleDto
import com.egormelnikoff.schedulerutmiit.domain.repos.ScheduleRemoteDataSource
import java.time.temporal.WeekFields
import javax.inject.Inject

class ScheduleApiNormalizer @Inject constructor(
    private val scheduleRemoteDataSource: ScheduleRemoteDataSource
) {
    suspend operator fun invoke(
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        schedule: ScheduleDto
    ): ScheduleDto {
        val fixedSchedule = when (schedule.timetable.type) {
            TimetableType.NON_PERIODIC, TimetableType.SESSION -> {
                ScheduleDto(
                    timetable = schedule.timetable,
                    periodic = null,
                    nonPeriodic = NonPeriodicContentDto(
                        events = schedule.nonPeriodic?.events
                            ?: schedule.periodic?.events
                    )
                )
            }

            TimetableType.PERIODIC -> {
                ScheduleDto(
                    timetable = schedule.timetable,
                    nonPeriodic = null,
                    periodic = if (schedule.periodic != null) {
                        schedule.periodic
                    } else {
                        val clearedEvents = schedule.nonPeriodic?.events
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

                        PeriodicContentDto(
                            events = checkedEvents,
                            recurrence = RecurrenceDto(
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