package com.egormelnikoff.schedulerutmiit.ui.state.actions.settings

import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel

data class SettingsActions(
    val onSetScheduleView: (Boolean) -> Unit, //isScheduleCalendar
    val onSendLogs: () -> Unit,
    val onSetShowCountClasses: (Boolean) -> Unit, //isShowCountClasses
    val onSetTheme: (String) -> Unit, // light | dark | system
    val onSetDecorColor: (Int) -> Unit, // 0 - 8 (From neutral to pink)
    val eventActions: EventActions
) {
    companion object {
        fun getSettingsActions(
            settingsViewModel: SettingsViewModel
        ) = SettingsActions(
            onSendLogs = {
                settingsViewModel.sendLogsFile()
            },
            eventActions = EventActions.getEventActions(settingsViewModel),
            onSetShowCountClasses = { value ->
                settingsViewModel.onSetShowCountClasses(value)
            },
            onSetTheme = { value ->
                settingsViewModel.onSetTheme(value)
            },
            onSetDecorColor = { value ->
                settingsViewModel.onSetDecorColor(value)
            },
            onSetScheduleView = { value ->
                settingsViewModel.onSetScheduleView(value)
            }
        )
    }
}

data class EventActions(
    val onSetEventView: (Boolean) -> Unit,
    val onSetEventGroupVisibility: (Boolean) -> Unit,
    val onSetEventRoomsVisibility: (Boolean) -> Unit,
    val onSetEventLecturersVisibility: (Boolean) -> Unit,
    val onSetEventTagVisibility: (Boolean) -> Unit,
    val onSetEventCommentVisibility: (Boolean) -> Unit,
) {
    companion object {
        fun getEventActions(
            settingsViewModel: SettingsViewModel
        ) = EventActions(
            onSetEventView = { visible ->
                settingsViewModel.onSetEventView(visible)
            },
            onSetEventGroupVisibility = { visible ->
                settingsViewModel.onSetEventGroupVisibility(visible)
            },
            onSetEventRoomsVisibility = { visible ->
                settingsViewModel.onSetEventRoomsVisibility(visible)
            },
            onSetEventLecturersVisibility = { visible ->
                settingsViewModel.onSetEventLecturersVisibility(visible)
            },
            onSetEventTagVisibility = { visible ->
                settingsViewModel.onSetEventTagVisibility(visible)
            },
            onSetEventCommentVisibility = { visible ->
                settingsViewModel.onSetEventCommentVisibility(visible)
            }
        )
    }
}