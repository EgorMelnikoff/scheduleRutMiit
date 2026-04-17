package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.Schedule

@Keep
data class NamedScheduleUiDto(
    val namedSchedule: NamedSchedule,
    val scheduleUiDto: ScheduleUiDto? = null
) {
    companion object {
        fun findCurrentSchedule(
            namedSchedule: NamedSchedule
        ): Schedule? {
            return namedSchedule.schedules.find { it.scheduleEntity.isDefault }
                ?: namedSchedule.schedules.firstOrNull()
        }

        operator fun invoke(
            namedSchedule: NamedSchedule?
        ): NamedScheduleUiDto? {
            namedSchedule ?: return null
            val currentSchedule = findCurrentSchedule(namedSchedule)
            currentSchedule ?: return NamedScheduleUiDto(
                namedSchedule = namedSchedule
            )

            return NamedScheduleUiDto(
                namedSchedule = namedSchedule,
                scheduleUiDto = ScheduleUiDto(
                    schedule = currentSchedule
                )
            )
        }
    }
}