package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import java.time.LocalDate
import java.time.LocalTime

sealed interface AddEventAction {
    data object OnBackClicked : AddEventAction
    data class OnNameChanged(val name: String) : AddEventAction
    data class OnTypeChanged(val type: String?) : AddEventAction
    data class OnDateChanged(val date: LocalDate) : AddEventAction
    data class OnIntervalChanged(val interval: Int) : AddEventAction
    data class OnPeriodChanged(val period: Int) : AddEventAction

    data class OnStartTimeChanged(val time: LocalTime) : AddEventAction
    data class OnEndTimeChanged(val time: LocalTime) : AddEventAction

    data object OnSaveClicked : AddEventAction

    data object OnAddRoom : AddEventAction
    data object OnAddLecturer : AddEventAction
    data object OnAddGroup : AddEventAction

    data class OnRemoveRoom(val index: Int) : AddEventAction
    data class OnRemoveLecturer(val index: Int) : AddEventAction
    data class OnRemoveGroup(val index: Int) : AddEventAction

    data class OnEditRoom(val index: Int, val room: Room) : AddEventAction
    data class OnEditLecturer(val index: Int, val lecturer: Lecturer) : AddEventAction
    data class OnEditGroup(val index: Int, val group: Group) : AddEventAction

    data class OnShowDialogDate(val show: Boolean) : AddEventAction
    data class OnShowDialogStartTime(val show: Boolean) : AddEventAction
    data class OnShowDialogEndTime(val show: Boolean) : AddEventAction
}