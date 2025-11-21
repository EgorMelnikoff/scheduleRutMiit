package com.egormelnikoff.schedulerutmiit.ui.navigation

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity

data class NavigationActions(
    val onBack: () -> Unit,
    val navigateToSchedule: () -> Unit,
    val navigateToSearch: () -> Unit,
    val navigateToEvent: (NavigateEventDialog) -> Unit,
    val navigateToRenameDialog: (NamedScheduleEntity) -> Unit,
    val navigateToAddSchedule: () -> Unit,
    val navigateToAddEvent: (ScheduleEntity) -> Unit,
    val navigateToHiddenEvents: (ScheduleEntity) -> Unit,
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
            navigateToEvent = { navigateEventDialog ->
                appBackStack.navigateToDialog(
                    dialog = Routes.EventDialog(
                        scheduleEntity = navigateEventDialog.scheduleEntity,
                        isSavedSchedule = navigateEventDialog.isSavedSchedule,
                        isCustomSchedule = navigateEventDialog.isCustomSchedule,
                        event = navigateEventDialog.event,
                        eventExtraData = navigateEventDialog.eventExtraData
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
            navigateToAddEvent = { scheduleEntity ->
                appBackStack.navigateToDialog(
                    dialog = Routes.AddEventDialog(
                        scheduleEntity = scheduleEntity
                    )
                )
            },
            navigateToHiddenEvents = { scheduleEntity ->
                appBackStack.navigateToDialog(
                    Routes.HiddenEventsDialog(
                        scheduleEntity = scheduleEntity
                    )
                )
            },
            navigateToNewsDialog = {
                appBackStack.navigateToDialog(Routes.NewsDialog)
            }
        )
    }
}

@Keep
data class NavigateEventDialog(
    val scheduleEntity: ScheduleEntity,
    val isSavedSchedule: Boolean,
    val isCustomSchedule: Boolean,
    val event: Event,
    val eventExtraData: EventExtraData?
)