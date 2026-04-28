package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.result.ScheduleUseCaseResult
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
            savedNamedSchedules = namedScheduleRepos.getAll(),
            namedScheduleWithSchedules = if (namedScheduleId == currentNamedScheduleId) {
                namedScheduleRepos.getById(namedScheduleId)
            } else null
        )
    }
}