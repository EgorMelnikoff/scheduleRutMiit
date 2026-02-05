package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.OpenSavedScheduleResult
import javax.inject.Inject

class OpenSavedScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater
) {
    suspend operator fun invoke(
        primaryKeyNamedSchedule: Long,
        setDefault: Boolean = false
    ): OpenSavedScheduleResult {
        if (setDefault) {
            scheduleRepos.updatePrioritySavedNamedSchedules(primaryKeyNamedSchedule)
            widgetDataUpdater.updateAll()
        }

        return OpenSavedScheduleResult(
            savedNamedSchedules = scheduleRepos.getSavedNamedSchedules(),
            namedScheduleFormatted = scheduleRepos.getNamedScheduleById(primaryKeyNamedSchedule)!!
        )
    }
}