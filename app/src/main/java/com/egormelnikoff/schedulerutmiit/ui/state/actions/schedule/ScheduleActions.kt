package com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule

import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import java.time.LocalDate

data class ScheduleActions(
    val eventActions: EventActions,
    val onGetNamedSchedule: (Triple<String, String, Int>) -> Unit, //Name, ApiId, Type
    val onSelectDefaultNamedSchedule: (Long) -> Unit, //NamedSchedulePK
    val onOpenNamedSchedule: (Long) -> Unit, //NamedSchedulePK
    val onSaveCurrentNamedSchedule: () -> Unit,
    val onDeleteNamedSchedule: (Pair<Long, Boolean>) -> Unit, //NamedSchedulePK, isDefault
    val onConfirmRenameNamedSchedule: (Pair<NamedScheduleEntity, String>) -> Unit, //NamedScheduleEntity, NewName

    val onLoadInitialScheduleData: () -> Unit,
    val onRefreshScheduleState: (Long) -> Unit, //showIsLoading

    val onSetDefaultSchedule: (Triple<Long, Long, String>) -> Unit, //NamedSchedulePK, SchedulePK, timetableId
    val onAddCustomSchedule: (Triple<String, LocalDate, LocalDate>) -> Unit, //Name, StartDate, EndDate
) {
    companion object {
        fun getScheduleActions(
            scheduleViewModel: ScheduleViewModel
        ) = ScheduleActions(
            eventActions = EventActions.getEventActions(
                scheduleViewModel = scheduleViewModel
            ),
            onGetNamedSchedule = { value ->
                scheduleViewModel.getNamedScheduleFromApi(
                    name = value.first,
                    apiId = value.second,
                    type = value.third
                )
            },
            onOpenNamedSchedule = { value ->
                scheduleViewModel.getNamedScheduleFromDb(
                    primaryKeyNamedSchedule = value
                )
            },
            onSelectDefaultNamedSchedule = { value ->
                scheduleViewModel.getNamedScheduleFromDb(
                    primaryKeyNamedSchedule = value,
                    setDefault = true
                )
            },
            onDeleteNamedSchedule = { value ->
                scheduleViewModel.deleteNamedSchedule(
                    primaryKeyNamedSchedule = value.first,
                    isDefault = value.second
                )
            },

            onLoadInitialScheduleData = {
                scheduleViewModel.refreshScheduleState(false)
            },
            onRefreshScheduleState = { primaryKey ->
                scheduleViewModel.refreshScheduleState(
                    showLoading = false,
                    updating = true,
                    primaryKeyNamedSchedule = primaryKey
                )
            },
            onSaveCurrentNamedSchedule = {
                scheduleViewModel.saveCurrentNamedSchedule()
            },

            onSetDefaultSchedule = { value ->
                scheduleViewModel.setDefaultSchedule(
                    primaryKeyNamedSchedule = value.first,
                    primaryKeySchedule = value.second,
                    timetableId = value.third
                )
            },

            onAddCustomSchedule = { value ->
                scheduleViewModel.addCustomNamedSchedule(
                    name = value.first,
                    startDate = value.second,
                    endDate = value.third,
                )
            },
            onConfirmRenameNamedSchedule = { newName ->
                scheduleViewModel.renameNamedSchedule(
                    namedScheduleEntity = newName.first,
                    newName = newName.second
                )
            }
        )
    }
}