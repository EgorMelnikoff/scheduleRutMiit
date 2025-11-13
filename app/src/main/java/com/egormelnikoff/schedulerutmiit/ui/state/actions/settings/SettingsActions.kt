package com.egormelnikoff.schedulerutmiit.ui.state.actions.settings

import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsState
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel

data class SettingsActions(
    val onSetScheduleView: (Boolean) -> Unit, //isScheduleCalendar
    val onLoadAppInfoState: () -> Unit,
    val onOpenUri: (String) -> Unit, //Uri
    val onSendLogs: () -> Unit,
    val onSetViewEvent: (Boolean) -> Unit, //isShortEvent
    val onSetShowCountClasses: (Boolean) -> Unit, //isShowCountClasses
    val onSetTheme: (String) -> Unit, // light | dark | system
    val onSetDecorColor: (Int) -> Unit // 0 - 8 (From neutral to pink)
) {
    companion object {
        fun getSettingsActions(
            settingsViewModel: SettingsViewModel,
            settingsState: SettingsState,
            logger: Logger,
            appUiState: AppUiState
        ) = SettingsActions(
            onLoadAppInfoState = {
                if (settingsState !is SettingsState.Loaded) {
                    settingsViewModel.getAppInfo()
                }
            },
            onOpenUri = { value ->
                appUiState.uriHandler.openUri(value)
            },

            onSendLogs = {
                logger.sendLogFile(appUiState.context)
            },
            onSetViewEvent = { value ->
                settingsViewModel.onSetViewEvent(value)
            },
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