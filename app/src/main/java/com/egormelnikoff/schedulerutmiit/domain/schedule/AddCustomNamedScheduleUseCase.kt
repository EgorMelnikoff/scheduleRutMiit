package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedSchedule
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.Schedule
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import java.time.LocalDate
import javax.inject.Inject

class AddCustomNamedScheduleUseCase @Inject constructor(
    private val saveNamedScheduleUseCase: SaveNamedScheduleUseCase,
) {
    suspend operator fun invoke(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): ScheduleUseCaseResult {
        val namedScheduleEntityEntity = NamedSchedule(
            namedScheduleEntity = NamedScheduleEntity(
                id = 0,
                fullName = name,
                shortName = name,
                apiId = null,
                type = NamedScheduleType.MY,
                isDefault = false,
                lastTimeUpdate = 0L
            ),
            schedules = listOf(
                Schedule(
                    scheduleEntity = ScheduleEntity(
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

        return saveNamedScheduleUseCase(namedScheduleEntityEntity)
    }
}