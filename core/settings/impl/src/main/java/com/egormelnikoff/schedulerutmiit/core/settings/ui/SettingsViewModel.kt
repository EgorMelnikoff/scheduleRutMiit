package com.egormelnikoff.schedulerutmiit.core.settings.ui

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.network.handler.errorHandler
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.Theme
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.core.settings.domain.use_case.CheckLatestReleaseUseCase
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEvent
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepos: SettingsRepos,
    private val checkLatestReleaseUseCase: CheckLatestReleaseUseCase
) : BaseViewModel<SettingsState, SettingsAction>(SettingsState()) {

    private val _uiEventFlow = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEventFlow.asSharedFlow()

    init {
        checkUpdates()

        settingsRepos.observeAppSettings().onEach { appSettings ->
            updateState {
                copy(
                    appSettings = appSettings
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnSetDecorColor -> onSetDecorColor(action.decorColor)
            is SettingsAction.OnSetEventCommentVisibility -> onSetEventCommentVisibility(action.visible)
            is SettingsAction.OnSetEventTagVisibility -> onSetEventTagVisibility(action.visible)
            is SettingsAction.OnSetEventGroupsVisibility -> onSetEventGroupVisibility(action.visible)
            is SettingsAction.OnSetEventLecturersVisibility -> onSetEventLecturersVisibility(action.visible)
            is SettingsAction.OnSetEventRoomsVisibility -> onSetEventRoomsVisibility(action.visible)
            is SettingsAction.OnSetEventExtraPolicy -> onSetEventExtraPolicy(action.eventExtraPolicy)
            is SettingsAction.OnSetEventsCountView -> onSetEventsCountView(action.eventsCountView)
            is SettingsAction.OnSetScheduleView -> onSetScheduleView(action.scheduleView)
            is SettingsAction.OnSetSchedulesDeletable -> onSetSchedulesDeletable(action.isDeletable)
            is SettingsAction.OnSetTheme -> onSetTheme(action.theme)
            is SettingsAction.OnSetUsedAmoled -> onSetUsedAmoled(action.usedAmoled)
            is SettingsAction.OnSetUsedImageInReview -> onSetUsedImageInReview(action.usedImage)
            is SettingsAction.OnSkipWelcomePage -> onSkipWelcomePage()
            is SettingsAction.OnSetActiveDialog -> updateState {
                copy(
                    activeDialog = action.activeDialog
                )
            }

            is SettingsAction.OnShowImportDialog -> updateState {
                copy(
                    importDialog = action.show
                )
            }
        }
    }

    private fun checkUpdates() {
        launchToCoroutine(
            handler = errorHandler(
                "CheckUpdate"
            )
        ) {
            updateState {
                copy(updatesAvailable = false)
            }

            val result = checkLatestReleaseUseCase()

            updateState {
                copy(updatesAvailable = result)
            }
        }
    }

    private fun onSetEventGroupVisibility(visible: Boolean) {
        viewModelScope.launch {
            settingsRepos.setEventGroupVisibility(visible)
        }
    }

    private fun onSetEventRoomsVisibility(visible: Boolean) {
        viewModelScope.launch {
            settingsRepos.setEventRoomsVisibility(visible)
        }
    }

    private fun onSetEventLecturersVisibility(visible: Boolean) {
        viewModelScope.launch {
            settingsRepos.setEventLecturersVisibility(visible)
        }
    }

    private fun onSetEventTagVisibility(visible: Boolean) {
        viewModelScope.launch {
            settingsRepos.setEventTagVisibility(visible)
        }
    }

    private fun onSetEventCommentVisibility(visible: Boolean) {
        viewModelScope.launch {
            settingsRepos.setEventCommentVisibility(visible)
        }
    }

    private fun onSetEventsCountView(
        eventsCountView: EventsCountView
    ) {
        viewModelScope.launch {
            settingsRepos.setEventCountView(eventsCountView)
        }
    }

    private fun onSetTheme(
        theme: Theme
    ) {
        viewModelScope.launch {
            settingsRepos.setTheme(theme)
        }
    }

    private fun onSetUsedImageInReview(
        usedImage: Boolean
    ) {
        viewModelScope.launch {
            settingsRepos.setUsedImageInReview(usedImage)
        }
    }

    private fun onSetUsedAmoled(
        usedAmoled: Boolean
    ) {
        viewModelScope.launch {
            settingsRepos.setUsedAmoled(usedAmoled)
        }
    }

    private fun onSetDecorColor(
        decorColor: Int
    ) {
        viewModelScope.launch {
            settingsRepos.setDecorColor(decorColor)
        }
    }

    private fun onSetScheduleView(
        scheduleView: ScheduleView
    ) {
        viewModelScope.launch {
            settingsRepos.setScheduleView(scheduleView)
        }
    }

    private fun onSetSchedulesDeletable(isDeletable: Boolean) {
        viewModelScope.launch {
            settingsRepos.setSchedulesDeletable(isDeletable)
        }
    }

    private fun onSkipWelcomePage() {
        viewModelScope.launch {
            settingsRepos.skipWelcomePage()
        }
    }

    private fun onSetEventExtraPolicy(eventExtraPolicy: EventExtraPolicy) {
        viewModelScope.launch {
            settingsRepos.setEventExtraPolicy(eventExtraPolicy)
        }
    }
}