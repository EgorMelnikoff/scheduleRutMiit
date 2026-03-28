package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import javax.inject.Inject

class DeleteNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val workScheduler: WorkScheduler
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        isDefault: Boolean
    ): ScheduleUseCaseResult {
        namedScheduleRepos.deleteById(namedScheduleId)
        val savedNamedSchedules = namedScheduleRepos.getAllEntities()
        if (savedNamedSchedules.isEmpty()) {
            workScheduler.cancelPeriodicScheduleUpdating()
            return ScheduleUseCaseResult(
                savedNamedSchedules = emptyList(),
                namedScheduleFormatted = null
            )
        }

        if (isDefault) {
            val namedSchedules = namedScheduleRepos.getAllEntities()
            if (namedSchedules.isNotEmpty()) {
                namedScheduleRepos.setDefaultNamedSchedule(namedSchedules[0].id)
            }
        }

        val defaultNamedSchedule = savedNamedSchedules.find { it.isDefault }
            ?: savedNamedSchedules.first()

        return ScheduleUseCaseResult(
            savedNamedSchedules = namedScheduleRepos.getAllEntities(),
            namedScheduleFormatted = namedScheduleRepos.getById(defaultNamedSchedule.id)
        )
    }
}