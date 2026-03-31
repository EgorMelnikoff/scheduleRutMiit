package com.egormelnikoff.schedulerutmiit.ui.navigation

import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity

sealed interface Route {
    sealed class Page(
        val index: Int
    ) : Route {
        data object Review : Page(0)
        data object Schedule : Page(1)
        data object NewsList : Page(2)
        data object Settings : Page(3)
    }

    sealed interface Dialog : Route {
        data object Empty : Dialog

        data class EventDialog(
            val namedScheduleEntity: NamedScheduleEntity,
            val scheduleEntity: ScheduleEntity,
            val isSavedSchedule: Boolean,
            val event: EventEntity,
            val eventExtraData: EventExtraData?
        ) : Dialog

        data class AddEventDialog(
            val namedScheduleEntity: NamedScheduleEntity,
            val scheduleEntity: ScheduleEntity,
            val event: EventEntity? = null
        ) : Dialog

        data class HiddenEventsDialog(
            val namedScheduleEntity: NamedScheduleEntity
        ) : Dialog

        data class RenameNamedScheduleDialog(
            val namedScheduleEntity: NamedScheduleEntity
        ) : Dialog

        data object NewsDialog : Dialog
        data object SearchDialog : Dialog
        data object CurriculumDialog : Dialog
        data object AddScheduleDialog : Dialog
    }
}