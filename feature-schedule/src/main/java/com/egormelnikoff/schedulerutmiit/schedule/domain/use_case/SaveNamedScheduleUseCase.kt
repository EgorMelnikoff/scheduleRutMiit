package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import javax.inject.Inject

class SaveNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val scheduleRepos: ScheduleRepos
) {
    suspend operator fun invoke(
        currentNamedScheduleWithSchedules: NamedScheduleWithSchedules
    ): Pair<Long, Boolean> {
        val namedScheduleId = namedScheduleRepos.save(
            currentNamedScheduleWithSchedules.namedSchedule
        )
        scheduleRepos.saveAll(
            namedScheduleId, currentNamedScheduleWithSchedules.schedulesWithEvents
        )

        if (namedScheduleRepos.getCount() == 1) {
            namedScheduleRepos.setDefaultNamedSchedule(namedScheduleId)
            return namedScheduleId to true
        }

        return namedScheduleId to false
    }
}