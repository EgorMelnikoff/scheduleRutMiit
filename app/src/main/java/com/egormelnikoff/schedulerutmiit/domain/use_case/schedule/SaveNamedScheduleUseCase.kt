package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.repos.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result.ScheduleUseCaseResult
import javax.inject.Inject

class SaveNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val scheduleRepos: ScheduleRepos,
    private val workScheduler: WorkScheduler
) {
    suspend operator fun invoke(
        currentNamedSchedule: NamedSchedule
    ): ScheduleUseCaseResult {
        val namedScheduleId = namedScheduleRepos.saveEntity(currentNamedSchedule.namedScheduleEntity)

        scheduleRepos.saveAllSchedules(
            namedScheduleId, currentNamedSchedule.schedules
        )

        if (namedScheduleRepos.getCount() == 1) {
            namedScheduleRepos.setDefaultNamedSchedule(namedScheduleId)
            workScheduler.startPeriodicScheduleUpdating()
        }


        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = namedScheduleRepos.getAllEntities(),
            namedSchedule = namedScheduleRepos.getById(namedScheduleId)
        )
    }
}