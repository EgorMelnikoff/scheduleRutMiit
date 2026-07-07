package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import javax.inject.Inject

class DeleteScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        currentNamedScheduleId: Long?,
        scheduleId: Long
    ) {
        scheduleRepos.deleteById(scheduleId)
    }
}