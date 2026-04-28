package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule.data.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.result.ScheduleUseCaseResult
import javax.inject.Inject

class DeleteNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        isDefault: Boolean
    ): ScheduleUseCaseResult {
        namedScheduleRepos.deleteById(namedScheduleId)
        val savedNamedSchedules = namedScheduleRepos.getAll()
        if (savedNamedSchedules.isEmpty()) {
            widgetDataUpdater.updateAll()
            return ScheduleUseCaseResult(
                savedNamedSchedules = listOf(),
                namedScheduleWithSchedules = null
            )
        }

        if (isDefault) {
            namedScheduleRepos.setDefaultNamedSchedule(savedNamedSchedules[0].id)
            widgetDataUpdater.updateAll()
        }

        namedScheduleRepos.getAll().let { namedSchedules ->
            val defaultNamedSchedule = namedSchedules.find { it.isDefault }
                ?: namedSchedules.firstOrNull()


            return ScheduleUseCaseResult(
                savedNamedSchedules = namedSchedules,
                namedScheduleWithSchedules = defaultNamedSchedule?.let {
                    namedScheduleRepos.getById(defaultNamedSchedule.id)
                }
            )
        }
    }
}