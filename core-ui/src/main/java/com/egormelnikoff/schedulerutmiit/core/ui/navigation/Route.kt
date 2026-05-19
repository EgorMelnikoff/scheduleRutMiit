package com.egormelnikoff.schedulerutmiit.core.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
sealed class Route : NavKey {
    sealed class Page(
        val index: Int
    ) : Route() {
        data object Review : Page(0)
        data object Schedule : Page(1)
        data object NewsList : Page(2)
        data object Settings : Page(3)
    }

    sealed class Dialog : Route() {
        data object Empty : Dialog()

        data class EventDialog(
            val namedSchedule: NamedSchedule,
            val schedule: Schedule,
            val isSavedSchedule: Boolean,
            val dateTime: LocalDateTime,
            val event: Event,
            val eventExtraData: EventExtraData?
        ) : Dialog()

        data class AddEventDialog(
            val namedSchedule: NamedSchedule,
            val schedule: Schedule,
            val event: Event? = null
        ) : Dialog()

        data class HiddenEventsDialog(
            val namedSchedule: NamedSchedule
        ) : Dialog()

        data class RenameNamedScheduleDialog(
            val namedSchedule: NamedSchedule
        ) : Dialog()

        data class NewsDialog(
            val newsId: Long
        ) : Dialog()

        data object SearchDialog : Dialog()
        data object CurriculumDialog : Dialog()
        data object AddScheduleDialog : Dialog()
    }
}