package com.egormelnikoff.schedulerutmiit.core.ui.navigation

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import java.time.LocalDateTime

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
            val namedSchedule: NamedSchedule,
            val schedule: Schedule,
            val isSavedSchedule: Boolean,
            val dateTime: LocalDateTime,
            val event: Event,
            val eventExtraData: EventExtraData?
        ) : Dialog

        data class AddEventDialog(
            val namedSchedule: NamedSchedule,
            val schedule: Schedule,
            val event: Event? = null
        ) : Dialog

        data class HiddenEventsDialog(
            val namedSchedule: NamedSchedule
        ) : Dialog

        data class RenameNamedScheduleDialog(
            val namedSchedule: NamedSchedule
        ) : Dialog

        data object NewsDialog : Dialog
        data object SearchDialog : Dialog
        data object CurriculumDialog : Dialog
        data object AddScheduleDialog : Dialog
    }
}