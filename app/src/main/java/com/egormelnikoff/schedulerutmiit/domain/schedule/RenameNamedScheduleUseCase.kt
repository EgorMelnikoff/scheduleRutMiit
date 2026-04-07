package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import javax.inject.Inject

class RenameNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos
) {
    suspend operator fun invoke(
        namedScheduleEntity: NamedScheduleEntity,
        currentNamedScheduleId: Long?,
        newName: String
    ): ScheduleUseCaseResult {
        namedScheduleRepos.updateName(
            namedScheduleId = namedScheduleEntity.id,
            newName = newName
        )


        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = namedScheduleRepos.getAllEntities(),
            namedSchedule = if (namedScheduleEntity.id == currentNamedScheduleId) {
                namedScheduleRepos.getById(namedScheduleEntity.id)
            } else null
        )
    }
}