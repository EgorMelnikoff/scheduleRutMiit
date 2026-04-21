package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.app.work.WorkScheduler
import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.ui.widget.WidgetDataUpdater
import javax.inject.Inject

class DeleteNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val workScheduler: WorkScheduler,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        isDefault: Boolean
    ): ScheduleUseCaseResult {
        namedScheduleRepos.deleteById(namedScheduleId)
        val savedNamedSchedules = namedScheduleRepos.getAllEntities()
        if (savedNamedSchedules.isEmpty()) {
            workScheduler.cancelPeriodicScheduleUpdating()
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