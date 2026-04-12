package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
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