package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import javax.inject.Inject

class DeleteScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val scheduleRepos: ScheduleRepos
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        currentNamedScheduleId: Long?,
        scheduleId: Long
    ): NamedScheduleWithSchedules? {
        scheduleRepos.deleteById(scheduleId)

        return if (namedScheduleId == currentNamedScheduleId) {
            namedScheduleRepos.getById(namedScheduleId)
        } else null
    }
}