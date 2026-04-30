package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule.domain.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import javax.inject.Inject


class SetDefaultScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        currentNamedScheduleWithSchedules: NamedScheduleWithSchedules,
        scheduleId: Long,
        timetableId: String,
        isSaved: Boolean
    ): NamedScheduleWithSchedules {
        if (isSaved) {
            scheduleRepos.setDefault(
                namedScheduleId = currentNamedScheduleWithSchedules.namedSchedule.id,
                scheduleId = scheduleId
            )
            widgetDataUpdater.updateAll()
        }

        val updatedSchedules =
            currentNamedScheduleWithSchedules.scheduleWithEvents.map { schedule ->
                schedule.copy(
                    schedule = schedule.schedule.copy(
                        isDefault = schedule.schedule.timetableId == timetableId
                    )
                )
            }

        return currentNamedScheduleWithSchedules.copy(scheduleWithEvents = updatedSchedules)
    }
}