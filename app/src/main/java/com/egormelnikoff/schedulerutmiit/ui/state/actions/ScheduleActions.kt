package com.egormelnikoff.schedulerutmiit.ui.state.actions

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import java.time.LocalDate

data class ScheduleActions(
    val eventActions: EventActions,
    val cancelLoading: () -> Unit,
    val onGetNamedSchedule: (String, String, NamedScheduleType) -> Unit, //Name, ApiId, Type
    val onSelectDefaultNamedSchedule: (Long) -> Unit, //NamedSchedulePK
    val onOpenNamedSchedule: (Long) -> Unit, //NamedSchedulePK
    val onSaveCurrentNamedSchedule: () -> Unit,
    val onDeleteNamedSchedule: (Long, Boolean) -> Unit, //NamedSchedulePK, isDefault
    val onConfirmRenameNamedSchedule: (NamedScheduleEntity, String) -> Unit, //NamedScheduleEntity, NewName
    val onAddCustomNamedSchedule: (String, LocalDate, LocalDate) -> Unit, //Name, StartDate, EndDate,

    val onLoadInitialScheduleData: () -> Unit,
    val onRefreshScheduleState: (Long, Boolean) -> Unit, //NamedSchedulePK

    val onSetDefaultSchedule: (Long, String) -> Unit, //SchedulePK, timetableId

) {
    companion object {
        operator fun invoke(
            scheduleViewModel: ScheduleViewModel
        ) = ScheduleActions(
            eventActions = EventActions(
                scheduleViewModel = scheduleViewModel
            ),
            cancelLoading = {
                scheduleViewModel.cancelLoading()
            },
            onGetNamedSchedule = { name, apiId, type ->
                scheduleViewModel.fetchNamedSchedule(
                    name = name,
                    apiId = apiId,
                    type = type
                )
            },

            onSaveCurrentNamedSchedule = {
                scheduleViewModel.saveCurrentNamedSchedule()
            },
            onOpenNamedSchedule = { value ->
                scheduleViewModel.getSavedNamedSchedule(
                    primaryKeyNamedSchedule = value
                )
            },
            onSelectDefaultNamedSchedule = { value ->
                scheduleViewModel.getSavedNamedSchedule(
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
            onAddCustomNamedSchedule = { name, startDate, endDate ->
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
            },

            onLoadInitialScheduleData = {
                scheduleViewModel.refreshScheduleState(showLoading = false)
            },
            onRefreshScheduleState = { primaryKey, updating ->
                scheduleViewModel.refreshScheduleState(
                    showLoading = false,
                    updating = updating,
                    primaryKeyNamedSchedule = primaryKey
                )
            },

            onSetDefaultSchedule = { primaryKeySchedule, timetableId ->
                scheduleViewModel.setDefaultSchedule(
                    primaryKeySchedule = primaryKeySchedule,
                    timetableId = timetableId
                )
            }
        )
    }
}