package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.WidgetDataUpdater
import javax.inject.Inject

internal class SetDefaultNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        namedScheduleId: Long
    ) {
        namedScheduleRepos.setDefault(namedScheduleId)
        widgetDataUpdater.updateAll()
    }
}
