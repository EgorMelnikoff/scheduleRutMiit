package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.yearDateMonthFormatter
import com.egormelnikoff.schedulerutmiit.core.common.dto.schedule.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.core.common.dto.schedule.NonPeriodicContentDto
import com.egormelnikoff.schedulerutmiit.core.common.dto.schedule.PeriodicContentDto
import com.egormelnikoff.schedulerutmiit.core.common.dto.schedule.ScheduleDto
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRemoteDataSource
import com.egormelnikoff.schedulerutmiit.schedule.extension.getTimeSlotName
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