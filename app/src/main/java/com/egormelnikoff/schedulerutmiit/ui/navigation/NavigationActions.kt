package com.egormelnikoff.schedulerutmiit.ui.navigation

import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity

data class NavigationActions(
    val onBack: () -> Unit,
    val navigateToSchedule: () -> Unit,
    val navigateToSearch: () -> Unit,
    val navigateToEvent: (ScheduleEntity, Boolean,  Event, EventExtraData?) -> Unit,
    val navigateToRenameDialog: (NamedScheduleEntity) -> Unit,
    val navigateToAddSchedule: () -> Unit,
    val navigateToAddEvent: (ScheduleEntity) -> Unit,
    val navigateToEditEvent: (ScheduleEntity, Event) -> Unit,
    val navigateToHiddenEvents: (ScheduleEntity) -> Unit,
    val navigateToInfoDialog: () -> Unit,
    val navigateToNewsDialog: () -> Unit
) {
    companion object {
        fun NavigationActions(
            appBackStack: AppBackStack<Route>
        ) = NavigationActions(
            onBack = { appBackStack.onBack() },
            navigateToSearch = {
                appBackStack.navigateToDialog(Route.Dialog.SearchDialog)
            },
            navigateToSchedule = {
                appBackStack.navigateToPage(Route.Page.Schedule)
            },
            navigateToAddSchedule = {
                appBackStack.navigateToDialog(Route.Dialog.AddScheduleDialog)
            },
            navigateToEvent = { scheduleEntity, isSavedSchedule, event, eventExtraData ->
                appBackStack.navigateToDialog(
                    dialog = Route.Dialog.EventDialog(
                        scheduleEntity = scheduleEntity,
                        isSavedSchedule = isSavedSchedule,
                        event = event,
                        eventExtraData = eventExtraData
                    )
                )
            },
            navigateToRenameDialog = { namedScheduleEntity ->
                appBackStack.navigateToDialog(
                    dialog = Route.Dialog.RenameNamedScheduleDialog(
                        namedScheduleEntity = namedScheduleEntity
                    )
                )
            },
            navigateToInfoDialog = {
                appBackStack.navigateToDialog(Route.Dialog.InfoDialog)
            },
            navigateToAddEvent = { scheduleEntity ->
                appBackStack.navigateToDialog(
                    dialog = Route.Dialog.AddEventDialog(
                        scheduleEntity = scheduleEntity
                    )
                )
            },
            navigateToEditEvent = { scheduleEntity, event ->
                appBackStack.navigateToDialog(
                    dialog = Route.Dialog.AddEventDialog(
                        event = event,
                        scheduleEntity = scheduleEntity
                    )
                )
            },
            navigateToHiddenEvents = { scheduleEntity ->
                appBackStack.navigateToDialog(
                    Route.Dialog.HiddenEventsDialog(
                        scheduleEntity = scheduleEntity
                    )
                )
            },
            navigateToNewsDialog = {
                appBackStack.navigateToDialog(Route.Dialog.NewsDialog)
            }
        )
    }
}