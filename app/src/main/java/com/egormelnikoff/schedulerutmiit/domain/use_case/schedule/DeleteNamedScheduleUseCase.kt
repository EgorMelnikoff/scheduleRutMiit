package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result.ScheduleUseCaseResult
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
                savedNamedScheduleEntities = emptyList(),
                namedSchedule = null
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
            savedNamedScheduleEntities = namedScheduleRepos.getAllEntities(),
            namedSchedule = namedScheduleRepos.getById(defaultNamedSchedule.id)
        )
    }
}