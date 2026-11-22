package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import android.net.Uri
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import java.time.LocalDate

internal sealed interface ScheduleAction {
    data object OnP2R : ScheduleAction
    data class OnViewClicked(
        val view: ScheduleView
    ) : ScheduleAction

    data object OnTitleClicked : ScheduleAction
    data object OnDismissSchedulesSheet : ScheduleAction
    data class OnChangeSchedule(
        val id: Long
    ) : ScheduleAction

    data object OnMenuVertClicked : ScheduleAction
    data object OnDismissScheduleSheet : ScheduleAction
    data class OnSetDefaultSchedule(
        val scheduleId: Long,
        val timetableId: String
    ) : ScheduleAction

    data object OnSaveScheduleClicked : ScheduleAction
    data object OnDeleteNamedSchedule : ScheduleAction

    data object OnImportClicked : ScheduleAction
    data object OnDismissImportSheet : ScheduleAction
    data class OnImportData(
        val uri: Uri?
    ) : ScheduleAction

    data class OnExportData(
        val uri: Uri
    ) : ScheduleAction

    data object OnReturnToDefaultClicked : ScheduleAction
    data object OnDismissReturnToDefault : ScheduleAction
    data object OnReturnToDefault : ScheduleAction

    data class OnEventClicked(
        val id: Long,
        val date: LocalDate?
    ) : ScheduleAction

    data class OnEditEventClicked(
        val eventId: Long?,
        val scheduleId: Long
    ) : ScheduleAction

    data class OnHideEvent(
        val id: Long
    ) : ScheduleAction

    data class OnDeleteEvent(
        val id: Long
    ) : ScheduleAction

    data object OnCancelLoading : ScheduleAction
    data object OnCancelRefresh : ScheduleAction

    data class OnOpenDialog(
        val dialog: Route.Dialog
    ) : ScheduleAction
}
