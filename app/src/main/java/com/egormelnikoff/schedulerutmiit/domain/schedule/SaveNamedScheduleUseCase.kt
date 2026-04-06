package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.repos.schedule.ScheduleRepos
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