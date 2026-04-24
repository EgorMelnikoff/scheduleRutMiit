package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.result.ScheduleUseCaseResult
import javax.inject.Inject

class RenameNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        currentNamedScheduleId: Long?,
        newName: String
    ): ScheduleUseCaseResult {
        namedScheduleRepos.updateName(
            namedScheduleId = namedScheduleId,
            newName = newName
        )

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = namedScheduleRepos.getAllEntities(),
            namedSchedule = if (namedScheduleId == currentNamedScheduleId) {
                namedScheduleRepos.getById(namedScheduleId)
            } else null
        )
    }
}