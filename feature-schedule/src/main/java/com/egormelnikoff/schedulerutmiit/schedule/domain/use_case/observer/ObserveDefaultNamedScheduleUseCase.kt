package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.observer

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import javax.inject.Inject

class ObserveDefaultNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos
) {
    operator fun invoke() = namedScheduleRepos.observeDefault()
}