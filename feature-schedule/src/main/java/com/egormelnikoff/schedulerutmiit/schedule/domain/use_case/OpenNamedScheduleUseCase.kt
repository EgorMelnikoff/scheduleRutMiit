package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.schedule.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.schedule.work.Scheduler
import javax.inject.Inject

class OpenNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater,
    private val scheduler: Scheduler,
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        setDefault: Boolean = false
    ): ScheduleUseCaseResult {
        if (setDefault) {
            namedScheduleRepos.setDefaultNamedSchedule(namedScheduleId)
            widgetDataUpdater.updateAll()
            scheduler.startPeriodicScheduleUpdating()
        }

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = namedScheduleRepos.getAllEntities(),
            namedSchedule = namedScheduleRepos.getById(namedScheduleId)
        )
    }
}