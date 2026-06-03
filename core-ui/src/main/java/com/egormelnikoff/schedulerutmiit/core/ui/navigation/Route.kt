package com.egormelnikoff.schedulerutmiit.core.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.Recurrence
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import kotlinx.serialization.Serializable
import java.time.LocalDate

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
            val namedScheduleId: Long,
            val schedule: Schedule,
            val isSavedSchedule: Boolean,
            val event: Event,
            val eventExtraData: EventExtraData?
        ) : Dialog()

        data class AddEditEventDialog(
            val namedScheduleId: Long,
            val scheduleId: Long,
            val recurrence: Recurrence?,
            val scheduleStartDate: LocalDate,
            val scheduleEndDate: LocalDate,
            val updatableEvent: Event? = null
        ) : Dialog()

        data class HiddenEventsDialog(
            val namedScheduleId: Long,
            val namedScheduleShortName: String,
            val timetableType: TimetableType?
        ) : Dialog()

        data class RenameNamedScheduleDialog(
            val namedScheduleId: Long,
            val namedScheduleFullName: String
        ) : Dialog()

        data class NewsDialog(
            val newsId: Long
        ) : Dialog()

        data object SearchDialog : Dialog()
        data object CurriculumDialog : Dialog()
        data object AddScheduleDialog : Dialog()
    }
}