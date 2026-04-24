package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.schedule.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.schedule.work.Scheduler
import javax.inject.Inject

class DeleteNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val scheduler: Scheduler,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        isDefault: Boolean
    ): ScheduleUseCaseResult {
        namedScheduleRepos.deleteById(namedScheduleId)
        val savedNamedSchedules = namedScheduleRepos.getAllEntities()
        if (savedNamedSchedules.isEmpty()) {
            scheduler.cancelPeriodicScheduleUpdating()
            widgetDataUpdater.updateAll()
            return ScheduleUseCaseResult(
                savedNamedScheduleEntities = listOf(),
                namedSchedule = null
            )
        }

        if (isDefault) {
            namedScheduleRepos.setDefaultNamedSchedule(savedNamedSchedules[0].id)
            widgetDataUpdater.updateAll()
        }

        namedScheduleRepos.getAllEntities().let { namedScheduleEntities ->
            val defaultNamedSchedule = namedScheduleEntities.find { it.isDefault }
                ?: namedScheduleEntities.firstOrNull()


            return ScheduleUseCaseResult(
                savedNamedScheduleEntities = namedScheduleEntities,
                namedSchedule = defaultNamedSchedule?.let {
                    namedScheduleRepos.getById(defaultNamedSchedule.id)
                }
            )
        }
    }
}