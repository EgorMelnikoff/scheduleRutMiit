package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.widget.WidgetDataUpdater
import javax.inject.Inject

class OpenNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        setDefault: Boolean = false
    ): NamedScheduleWithSchedules {
        if (setDefault) {
            namedScheduleRepos.setDefaultNamedSchedule(namedScheduleId)
            widgetDataUpdater.updateAll()
        }

        return namedScheduleRepos.getById(namedScheduleId)
    }
}