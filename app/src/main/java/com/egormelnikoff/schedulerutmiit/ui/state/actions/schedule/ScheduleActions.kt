package com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule

import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import java.time.LocalDate

data class ScheduleActions(
    val eventActions: EventActions,
    val onGetNamedSchedule: (String, String, Int) -> Unit, //Name, ApiId, Type
    val onSelectDefaultNamedSchedule: (Long) -> Unit, //NamedSchedulePK
    val onOpenNamedSchedule: (Long) -> Unit, //NamedSchedulePK
    val onSaveCurrentNamedSchedule: () -> Unit,
    val onDeleteNamedSchedule: (Long, Boolean) -> Unit, //NamedSchedulePK, isDefault
    val onConfirmRenameNamedSchedule: (NamedScheduleEntity, String) -> Unit, //NamedScheduleEntity, NewName

    val onLoadInitialScheduleData: () -> Unit,
    val onRefreshScheduleState: (Long) -> Unit, //NamedSchedulePK

    val onSetDefaultSchedule: (Long, Long, String) -> Unit, //NamedSchedulePK, SchedulePK, timetableId
    val onAddCustomSchedule: (String, LocalDate, LocalDate) -> Unit, //Name, StartDate, EndDate
) {
    companion object {
        fun getScheduleActions(
            scheduleViewModel: ScheduleViewModel
        ) = ScheduleActions(
            eventActions = EventActions.getEventActions(
                scheduleViewModel = scheduleViewModel
            ),
            onGetNamedSchedule = { name, apiId, type ->
                scheduleViewModel.getNamedScheduleFromApi(
                    name = name,
                    apiId = apiId,
                    type = type
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
            onDeleteNamedSchedule = { primaryKeyNamedSchedule, isDefault ->
                scheduleViewModel.deleteNamedSchedule(
                    primaryKeyNamedSchedule = primaryKeyNamedSchedule,
                    isDefault = isDefault
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

            onSetDefaultSchedule = { primaryKeyNamedSchedule, primaryKeySchedule, timetableId ->
                scheduleViewModel.setDefaultSchedule(
                    primaryKeyNamedSchedule = primaryKeyNamedSchedule,
                    primaryKeySchedule = primaryKeySchedule,
                    timetableId = timetableId
                )
            },

            onAddCustomSchedule = { name, startDate, endDate ->
                scheduleViewModel.addCustomNamedSchedule(
                    name = name,
                    startDate = startDate,
                    endDate = endDate,
                )
            },
            onConfirmRenameNamedSchedule = { namedScheduleEntity, newName ->
                scheduleViewModel.renameNamedSchedule(
                    namedScheduleEntity = namedScheduleEntity,
                    newName = newName
                )
            }
        )
    }
}