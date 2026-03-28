package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import javax.inject.Inject

class OpenNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater,
    private val workScheduler: WorkScheduler,
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        setDefault: Boolean = false
    ): ScheduleUseCaseResult {
        if (setDefault) {
            namedScheduleRepos.setDefaultNamedSchedule(namedScheduleId)
            widgetDataUpdater.updateAll()
            workScheduler.startPeriodicScheduleUpdating()
        }

        return ScheduleUseCaseResult(
            savedNamedSchedules = namedScheduleRepos.getAllEntities(),
            namedScheduleFormatted = namedScheduleRepos.getById(namedScheduleId)
        )
    }
}