package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.widget.WidgetDataUpdater
import javax.inject.Inject

class SetDefaultNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        namedScheduleId: Long
    ) {
        namedScheduleRepos.setDefaultNamedSchedule(namedScheduleId)
        widgetDataUpdater.updateAll()
    }
}