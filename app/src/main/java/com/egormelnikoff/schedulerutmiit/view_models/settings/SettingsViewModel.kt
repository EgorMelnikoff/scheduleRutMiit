package com.egormelnikoff.schedulerutmiit.view_models.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.app.preferences.EventView
import com.egormelnikoff.schedulerutmiit.app.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val logger: Logger,
    private val preferencesDataStore: PreferencesDataStore,
) : ViewModel() {
    private val _appSettings = MutableStateFlow<AppSettings?>(null)
    val appSettings: StateFlow<AppSettings?> = _appSettings

    init {
        collectSettings()
    }

    fun sendLogsFile() {
        logger.sendLogsFile()
    }

    private fun collectSettings() {
        viewModelScope.launch {
            val eventFlow = combine(
                preferencesDataStore.groupsVisibilityFlow,
                preferencesDataStore.roomsVisibilityFlow,
                preferencesDataStore.lecturersVisibilityFlow,
                preferencesDataStore.tagVisibilityFlow,
                preferencesDataStore.commentVisibilityFlow
            ) { groupsVisibility, roomsVisibility, lecturersVisibility, tagVisibility, commentVisibility ->
                EventView(
                    groupsVisible = groupsVisibility,
                    roomsVisible = roomsVisibility,
                    lecturersVisible = lecturersVisibility,
                    tagVisible = tagVisibility,
                    commentVisible = commentVisibility
                )
            }

            val themeFlow = combine(
                preferencesDataStore.themeFlow,
                preferencesDataStore.decorColorFlow,
            ) { theme, decorColorIndex ->
                Pair(theme, decorColorIndex)
            }

            combine(
                themeFlow,
                eventFlow,
                preferencesDataStore.scheduleViewFlow,
                preferencesDataStore.schedulesDeletableFlow,
                preferencesDataStore.showCountClassesFlow,
            ) { theme, eventView, scheduleView, schedulesDeletable, isShowCountClasses ->
                AppSettings(
                    theme = theme.first,
                    decorColorIndex = theme.second,
                    scheduleView = scheduleView,
                    schedulesDeletable = schedulesDeletable,
                    showCountClasses = isShowCountClasses,
                    eventView = eventView
                )
            }.collect { settings ->
                _appSettings.value = settings
            }
        }
    }

    fun onSetEventGroupVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventGroupVisibility(visible)
        }
    }

    fun onSetEventView(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventGroupVisibility(visible)
            preferencesDataStore.setEventRoomsVisibility(visible)
            preferencesDataStore.setEventLecturersVisibility(visible)
            preferencesDataStore.setEventTagVisibility(visible)
            preferencesDataStore.setEventCommentVisibility(visible)
        }
    }

    fun onSetEventRoomsVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventRoomsVisibility(visible)
        }
    }

    fun onSetEventLecturersVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventLecturersVisibility(visible)
        }
    }

    fun onSetEventTagVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventTagVisibility(visible)
        }
    }

    fun onSetEventCommentVisibility(visible: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setEventCommentVisibility(visible)
        }
    }

    fun onSetShowCountClasses(
        showCountClasses: Boolean
    ) {
        viewModelScope.launch {
            preferencesDataStore.setShowCountClasses(showCountClasses)
        }
    }

    fun onSetTheme(
        theme: String
    ) {
        viewModelScope.launch {
            preferencesDataStore.setTheme(theme)
        }
    }

    fun onSetDecorColor(
        decorColor: Int
    ) {
        viewModelScope.launch {
            preferencesDataStore.setDecorColor(decorColor)
        }
    }

    fun onSetScheduleView(
        scheduleView: ScheduleView
    ) {
        viewModelScope.launch {
            preferencesDataStore.setScheduleView(scheduleView)
        }
    }

    fun onSetSchedulesDeletable(isDeletable: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setSchedulesDeletable(isDeletable)
        }
    }
}