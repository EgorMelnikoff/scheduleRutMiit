package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import java.time.LocalDate
import javax.inject.Inject

class AddCustomNamedScheduleUseCase @Inject constructor(
    private val saveNamedScheduleUseCase: SaveNamedScheduleUseCase,
) {
    suspend operator fun invoke(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): NamedScheduleWithSchedules {
        return saveNamedScheduleUseCase(
            currentNamedScheduleWithSchedules = NamedScheduleWithSchedules(
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
                            timetableType = TimetableType.NON_PERIODIC,
                            downloadUrl = null,
                            startDate = startDate,
                            endDate = endDate,
                            recurrence = null
                        ),
                        events = listOf(),
                        eventsExtraData = listOf()
                    )
                )
            )
        )
    }
}