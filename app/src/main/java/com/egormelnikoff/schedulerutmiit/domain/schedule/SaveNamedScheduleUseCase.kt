package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.local.ScheduleLocalRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import javax.inject.Inject

class SaveNamedScheduleUseCase @Inject constructor(
    private val scheduleLocalRepos: ScheduleLocalRepos,
    private val workScheduler: WorkScheduler
) {
    suspend operator fun invoke(
        currentNamedSchedule: NamedScheduleFormatted
    ): ScheduleUseCaseResult {
        val namedScheduleId = scheduleLocalRepos.saveNamedSchedule(currentNamedSchedule)
        val namedSchedule = scheduleLocalRepos.getNamedScheduleById(namedScheduleId)

        if (namedSchedule.namedScheduleEntity.isDefault) {
            workScheduler.startPeriodicScheduleUpdating()
        }

        return ScheduleUseCaseResult(
            savedNamedSchedules = scheduleLocalRepos.getSavedNamedSchedules(),
            namedScheduleFormatted = namedSchedule
        )
    }
}