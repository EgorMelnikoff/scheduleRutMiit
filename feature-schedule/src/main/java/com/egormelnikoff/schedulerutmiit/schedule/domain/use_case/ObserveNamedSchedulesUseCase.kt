package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNamedSchedulesUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos
) {

    operator fun invoke(): Flow<List<NamedSchedule>> {
        return namedScheduleRepos.observeAll()
    }
}