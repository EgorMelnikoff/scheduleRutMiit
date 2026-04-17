package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result.ScheduleUseCaseResult
import javax.inject.Inject

class RenameNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos
) {
    suspend operator fun invoke(
        currentNamedScheduleId: Long,
        settledNamedScheduleId: Long?,
        newName: String
    ): ScheduleUseCaseResult {
        namedScheduleRepos.updateName(
            namedScheduleId = currentNamedScheduleId,
            newName = newName
        )


        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = namedScheduleRepos.getAllEntities(),
            namedSchedule = if (currentNamedScheduleId == settledNamedScheduleId) {
                namedScheduleRepos.getById(currentNamedScheduleId)
            } else null
        )
    }
}