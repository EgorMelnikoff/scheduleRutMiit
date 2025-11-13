package com.egormelnikoff.schedulerutmiit.ui.navigation

import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity

data class NavigationActions(
    val onBack: () -> Unit,
    val navigateToSchedule: () -> Unit,
    val navigateToSearch: () -> Unit,
    val navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    val navigateToRenameDialog: (NamedScheduleEntity) -> Unit,
    val navigateToAddSchedule: () -> Unit,
    val navigateToAddEvent: () -> Unit,
    val navigateToHiddenEvents: () -> Unit,
    val navigateToInfoDialog: () -> Unit,
    val navigateToNewsDialog: () -> Unit
) {
    companion object {
        fun getNavigationActions(
            appBackStack: AppBackStack<Routes>
        ) = NavigationActions(
            onBack = { appBackStack.onBack() },
            navigateToSearch = {
                appBackStack.navigateToDialog(Routes.SearchDialog)
            },
            navigateToSchedule = {
                appBackStack.navigateToPage(Routes.Schedule)
            },
            navigateToAddSchedule = {
                appBackStack.navigateToDialog(Routes.AddScheduleDialog)
            },
            navigateToEvent = { value ->
                appBackStack.navigateToDialog(
                    dialog = Routes.EventDialog(
                        event = value.first,
                        eventExtraData = value.second
                    )
                )
            },
            navigateToRenameDialog = { namedScheduleEntity ->
                appBackStack.navigateToDialog(
                    dialog = Routes.RenameNamedScheduleDialog(
                        namedScheduleEntity = namedScheduleEntity
                    )
                )
            },
            navigateToInfoDialog = {
                appBackStack.navigateToDialog(Routes.InfoDialog)
            },
            navigateToAddEvent = {
                appBackStack.navigateToDialog(Routes.AddEventDialog)
            },
            navigateToHiddenEvents = {
                appBackStack.navigateToDialog(Routes.HiddenEventsDialog)
            },
            navigateToNewsDialog = {
                appBackStack.navigateToDialog(Routes.NewsDialog)
            }
        )
    }
}