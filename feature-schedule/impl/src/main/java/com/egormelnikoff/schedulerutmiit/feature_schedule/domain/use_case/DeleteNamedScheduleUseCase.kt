package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.WidgetDataUpdater
import javax.inject.Inject

class DeleteNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        isDefault: Boolean
    ) {
        namedScheduleRepos.delete(namedScheduleId)
        val savedNamedSchedules = namedScheduleRepos.getAll()
        if (savedNamedSchedules.isEmpty()) {
            widgetDataUpdater.updateAll()
            return
        }

        if (isDefault) {
            namedScheduleRepos.setDefault(savedNamedSchedules[0].id)
            widgetDataUpdater.updateAll()
        }
    }
}