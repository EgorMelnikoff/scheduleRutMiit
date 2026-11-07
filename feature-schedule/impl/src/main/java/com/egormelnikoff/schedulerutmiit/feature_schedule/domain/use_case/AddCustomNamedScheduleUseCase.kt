package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Recurrence
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.TimetableType
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.NamedScheduleRepos
import java.time.LocalDate
import javax.inject.Inject

class AddCustomNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
) {
    suspend operator fun invoke(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate,
        timetableType: TimetableType
    ): Pair<Long, Boolean> {
        return namedScheduleRepos.save(
            namedScheduleWithSchedules = NamedScheduleWithSchedules(
                namedSchedule = NamedSchedule(
                    id = 0,
                    fullName = name,
                    shortName = name,
                    apiId = null,
                    type = NamedScheduleType.MY,
                    isDefault = false,
                    lastTimeUpdate = 0L
                ),
                schedulesWithEvents = listOf(
                    ScheduleWithEvents(
                        schedule = Schedule(
                            id = 0,
                            isDefault = true,
                            namedScheduleId = 0,
                            timetableId = "d=${startDate};t=2",
                            timetableType = timetableType,
                            downloadUrl = null,
                            startDate = startDate,
                            endDate = endDate,
                            recurrence = if (timetableType.isPeriodic()) {
                                Recurrence(
                                    interval = 2,
                                    firstWeekNumber = 1
                                )
                            } else null
                        ),
                        events = listOf(),
                        eventsExtraData = listOf()
                    )
                )
            )
        )
    }
}