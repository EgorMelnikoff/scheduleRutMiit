package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import javax.inject.Inject

class RenameNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        newName: String
    ) {
        namedScheduleRepos.updateName(
            namedScheduleId = namedScheduleId,
            newName = newName
        )
    }
}