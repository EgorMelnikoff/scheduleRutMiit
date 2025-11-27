package com.egormelnikoff.schedulerutmiit.ui.navigation

import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity

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
        data class EventDialog(
            val scheduleEntity: ScheduleEntity,
            val isSavedSchedule: Boolean,
            val event: Event,
            val eventExtraData: EventExtraData?
        ) : Dialog

        data class AddEventDialog(
            val event: Event? = null,
            val scheduleEntity: ScheduleEntity
        ) : Dialog

        data class HiddenEventsDialog(
            val scheduleEntity: ScheduleEntity
        ) : Dialog

        data class RenameNamedScheduleDialog(
            val namedScheduleEntity: NamedScheduleEntity
        ) : Dialog

        data object NewsDialog : Dialog
        data object InfoDialog : Dialog
        data object SearchDialog : Dialog
        data object AddScheduleDialog : Dialog
    }
}