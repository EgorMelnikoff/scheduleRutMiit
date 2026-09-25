package com.egormelnikoff.schedulerutmiit.core.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.egormelnikoff.schedulerutmiit.core.common.domain.NavigateSchedule
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
sealed class Route : NavKey {
    sealed class Page : Route() {
        data object Review : Page()
        data class Schedule(
            val navigateSchedule: NavigateSchedule? = null
        ) : Page()

        data object Tasks : Page()
        data object Settings : Page()
    }

    sealed class Dialog : Route() {
        data object Empty : Dialog()

        data object NewsList : Dialog()

        data class EventDialog(
            val eventId: Long,
            val date: LocalDate?
        ) : Dialog()

        data class EditTaskDialog(
            val taskId: Long,
            val date: LocalDate
        ) : Dialog()

        data class EditEventDialog(
            val eventId: Long?,
            val scheduleId: Long
        ) : Dialog()

        data class HiddenEventsDialog(
            val scheduleId: Long
        ) : Dialog()

        data class RenameNamedScheduleDialog(
            val namedScheduleId: Long
        ) : Dialog()

        data class NewsDialog(
            val newsId: Long
        ) : Dialog()

        data object SearchDialog : Dialog()
        data object CurriculumDialog : Dialog()
        data object AddScheduleDialog : Dialog()
        data object AddTaskDialog : Dialog()
    }
}