package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.TimetableType
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.OpenSavedScheduleResult
import java.time.LocalDate
import javax.inject.Inject

class AddCustomNamedScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos
) {
    suspend operator fun invoke(
        name: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): OpenSavedScheduleResult {
        val namedSchedule = NamedScheduleFormatted(
            namedScheduleEntity = NamedScheduleEntity(
                id = 0,
                fullName = name,
                shortName = name,
                apiId = null,
                type = NamedScheduleType.My,
                isDefault = false,
                lastTimeUpdate = 0L
            ),
            schedules = listOf(
                ScheduleFormatted(
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
        val namedScheduleId = scheduleRepos.insertNamedSchedule(namedSchedule)

        return OpenSavedScheduleResult(
            savedNamedSchedules = scheduleRepos.getSavedNamedSchedules(),
            namedScheduleFormatted = scheduleRepos.getNamedScheduleById(namedScheduleId)
        )
    }
}