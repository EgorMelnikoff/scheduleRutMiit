package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.local.ScheduleLocalRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import javax.inject.Inject

class DeleteNamedScheduleUseCase @Inject constructor(
    private val scheduleLocalRepos: ScheduleLocalRepos,
    private val workScheduler: WorkScheduler
) {
    suspend operator fun invoke(
        primaryKeyNamedSchedule: Long,
        isDefault: Boolean
    ): ScheduleUseCaseResult {
        scheduleLocalRepos.deleteNamedScheduleById(primaryKeyNamedSchedule, isDefault)
        val savedNamedSchedules = scheduleLocalRepos.getSavedNamedSchedules()
        if (savedNamedSchedules.isEmpty()) {
            workScheduler.cancelPeriodicScheduleUpdating()
            return ScheduleUseCaseResult(
                savedNamedSchedules = emptyList(),
                namedScheduleFormatted = null
            )
        }
        val defaultNamedSchedule = savedNamedSchedules.find { it.isDefault }
            ?: savedNamedSchedules.first()

        return ScheduleUseCaseResult(
            savedNamedSchedules = savedNamedSchedules,
            namedScheduleFormatted = scheduleLocalRepos.getNamedScheduleById(defaultNamedSchedule.id)
        )
    }
}