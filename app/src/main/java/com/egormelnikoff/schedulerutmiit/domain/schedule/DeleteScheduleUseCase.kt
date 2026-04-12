package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.repos.schedule.ScheduleRepos
import javax.inject.Inject

class DeleteScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val scheduleRepos: ScheduleRepos
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        currentNamedScheduleId: Long?,
        scheduleId: Long
    ): ScheduleUseCaseResult {
        scheduleRepos.deleteById(scheduleId)

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = namedScheduleRepos.getAllEntities(),
            namedSchedule = if (namedScheduleId == currentNamedScheduleId) {
                namedScheduleRepos.getById(namedScheduleId)
            } else null
        )
    }
}