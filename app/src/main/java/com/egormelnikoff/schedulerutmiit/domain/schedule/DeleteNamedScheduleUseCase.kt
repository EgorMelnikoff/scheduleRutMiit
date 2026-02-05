package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.OpenSavedScheduleResult
import javax.inject.Inject

class DeleteNamedScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val workScheduler: WorkScheduler
) {
    suspend operator fun invoke(
        primaryKeyNamedSchedule: Long,
        isDefault: Boolean
    ): OpenSavedScheduleResult {
        scheduleRepos.deleteNamedScheduleById(primaryKeyNamedSchedule, isDefault)
        val savedNamedSchedules = scheduleRepos.getSavedNamedSchedules()
        if (savedNamedSchedules.isEmpty()) {
            workScheduler.cancelPeriodicScheduleUpdating()
            return OpenSavedScheduleResult(
                savedNamedSchedules = emptyList(),
                namedScheduleFormatted = null
            )
        }
        val defaultNamedSchedule =
            savedNamedSchedules.find { it.isDefault } ?: savedNamedSchedules.first()

        return OpenSavedScheduleResult(
            savedNamedSchedules = savedNamedSchedules,
            namedScheduleFormatted = scheduleRepos.getNamedScheduleById(defaultNamedSchedule.id)
        )
    }
}