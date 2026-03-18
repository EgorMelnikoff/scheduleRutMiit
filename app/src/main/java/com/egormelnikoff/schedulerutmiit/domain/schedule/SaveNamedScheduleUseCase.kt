package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import javax.inject.Inject

class SaveNamedScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val workScheduler: WorkScheduler
) {
    suspend operator fun invoke(
        currentNamedSchedule: NamedScheduleFormatted
    ): ScheduleUseCaseResult {
        val namedScheduleId = scheduleRepos.insertNamedSchedule(currentNamedSchedule)
        val namedSchedule = scheduleRepos.getNamedScheduleById(namedScheduleId)

        if (namedSchedule.namedScheduleEntity.isDefault) {
            workScheduler.startPeriodicScheduleUpdating()
        }

        return ScheduleUseCaseResult(
            savedNamedSchedules = scheduleRepos.getSavedNamedSchedules(),
            namedScheduleFormatted = namedSchedule
        )
    }
}