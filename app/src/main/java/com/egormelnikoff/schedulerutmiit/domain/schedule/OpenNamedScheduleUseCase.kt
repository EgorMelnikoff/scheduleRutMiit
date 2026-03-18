package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import javax.inject.Inject

class OpenSavedScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater,
    private val workScheduler: WorkScheduler,
) {
    suspend operator fun invoke(
        primaryKeyNamedSchedule: Long,
        setDefault: Boolean = false
    ): ScheduleUseCaseResult {
        if (setDefault) {
            scheduleRepos.updatePrioritySavedNamedSchedules(primaryKeyNamedSchedule)
            widgetDataUpdater.updateAll()
            workScheduler.startPeriodicScheduleUpdating()
        }

        return ScheduleUseCaseResult(
            savedNamedSchedules = scheduleRepos.getSavedNamedSchedules(),
            namedScheduleFormatted = scheduleRepos.getNamedScheduleById(primaryKeyNamedSchedule)
        )
    }
}