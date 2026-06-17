package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import javax.inject.Inject

class RenameNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        currentNamedScheduleId: Long?,
        newName: String
    ): NamedScheduleWithSchedules? {
        namedScheduleRepos.updateName(
            namedScheduleId = namedScheduleId,
            newName = newName
        )

        return if (namedScheduleId == currentNamedScheduleId) {
            namedScheduleRepos.getById(namedScheduleId)
        } else null
    }
}