package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.ui.widget.WidgetDataUpdater
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
            savedNamedScheduleEntities = namedScheduleRepos.getAllEntities(),
            namedSchedule = namedScheduleRepos.getById(namedScheduleId)
        )
    }
}