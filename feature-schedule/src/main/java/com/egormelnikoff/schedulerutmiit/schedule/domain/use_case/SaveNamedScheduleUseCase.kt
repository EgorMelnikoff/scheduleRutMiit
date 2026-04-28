package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.result.ScheduleUseCaseResult
import javax.inject.Inject

class SaveNamedScheduleUseCase @Inject constructor(
    private val openNamedScheduleUseCase: OpenNamedScheduleUseCase,
    private val namedScheduleRepos: NamedScheduleRepos,
    private val scheduleRepos: ScheduleRepos
) {
    suspend operator fun invoke(
        currentNamedScheduleWithSchedules: NamedScheduleWithSchedules
    ): ScheduleUseCaseResult {
        val namedScheduleId =
            namedScheduleRepos.save(currentNamedScheduleWithSchedules.namedSchedule)
        scheduleRepos.saveAll(
            namedScheduleId, currentNamedScheduleWithSchedules.scheduleWithEvents
        )

        if (namedScheduleRepos.getCount() == 1) {
            return openNamedScheduleUseCase(namedScheduleId, true)
        }

        return openNamedScheduleUseCase(namedScheduleId)
    }
}