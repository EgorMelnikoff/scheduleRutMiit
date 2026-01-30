package com.egormelnikoff.schedulerutmiit.ui.navigation

import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity

data class NavigationActions(
    val onBack: () -> Unit,
    val navigateToSchedule: () -> Unit,
    val navigateToSearch: () -> Unit,
    val navigateToEvent: (ScheduleEntity, Boolean, Event, EventExtraData?) -> Unit,
    val navigateToRenameDialog: (NamedScheduleEntity) -> Unit,
    val navigateToAddSchedule: () -> Unit,
    val navigateToAddEvent: (ScheduleEntity) -> Unit,
    val navigateToEditEvent: (ScheduleEntity, Event) -> Unit,
    val navigateToHiddenEvents: (ScheduleEntity) -> Unit,
    val navigateToInfoDialog: () -> Unit,
    val navigateToNewsDialog: () -> Unit,
    val navigateToCurriculumDialog: () -> Unit
) {
    companion object {
        fun NavigationActions(
            appBackStack: AppBackStack<Route.Page>
        ) = NavigationActions(
            onBack = { appBackStack.onBack() },
            navigateToSearch = {
                appBackStack.openDialog(Route.Dialog.SearchDialog)
            },
            navigateToSchedule = {
                if (appBackStack.lastPage() != Route.Page.Schedule) {
                    appBackStack.openPage(Route.Page.Schedule)
                }
            },
            navigateToAddSchedule = {
                appBackStack.openDialog(Route.Dialog.AddScheduleDialog)
            },
            navigateToEvent = { scheduleEntity, isSavedSchedule, event, eventExtraData ->
                appBackStack.openDialog(
                    dialog = Route.Dialog.EventDialog(
                        scheduleEntity = scheduleEntity,
                        isSavedSchedule = isSavedSchedule,
                        event = event,
                        eventExtraData = eventExtraData
                    )
                )
            },
            navigateToRenameDialog = { namedScheduleEntity ->
                appBackStack.openDialog(
                    dialog = Route.Dialog.RenameNamedScheduleDialog(
                        namedScheduleEntity = namedScheduleEntity
                    )
                )
            },
            navigateToInfoDialog = {
                appBackStack.openDialog(Route.Dialog.InfoDialog)
            },
            navigateToAddEvent = { scheduleEntity ->
                appBackStack.openDialog(
                    dialog = Route.Dialog.AddEventDialog(
                        scheduleEntity = scheduleEntity
                    )
                )
            },
            navigateToEditEvent = { scheduleEntity, event ->
                appBackStack.openDialog(
                    dialog = Route.Dialog.AddEventDialog(
                        event = event,
                        scheduleEntity = scheduleEntity
                    )
                )
            },
            navigateToHiddenEvents = { scheduleEntity ->
                appBackStack.openDialog(
                    Route.Dialog.HiddenEventsDialog(
                        scheduleEntity = scheduleEntity
                    )
                )
            },
            navigateToNewsDialog = {
                appBackStack.openDialog(Route.Dialog.NewsDialog)
            },
            navigateToCurriculumDialog = {
                appBackStack.openDialog(Route.Dialog.CurriculumDialog)
            }
        )
    }
}