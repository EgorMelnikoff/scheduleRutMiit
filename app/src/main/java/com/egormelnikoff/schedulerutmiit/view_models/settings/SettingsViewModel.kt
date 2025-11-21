package com.egormelnikoff.schedulerutmiit.view_models.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.EventView
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.repos.settings.SettingsRepos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SettingsViewModel {
    val appSettings: StateFlow<AppSettings?>
    fun sendLogsFile()
    fun onSetShowCountClasses(showCountClasses: Boolean)
    fun onSetTheme(theme: String)
    fun onSetDecorColor(decorColor: Int)
    fun onSetScheduleView(scheduleView: Boolean)

    fun onSetEventView(visible: Boolean)
    fun onSetEventGroupVisibility(visible: Boolean)
    fun onSetEventRoomsVisibility(visible: Boolean)
    fun onSetEventLecturersVisibility(visible: Boolean)
    fun onSetEventTagVisibility(visible: Boolean)
    fun onSetEventCommentVisibility(visible: Boolean)
}

@HiltViewModel
class SettingsViewModelImpl @Inject constructor(
    private val settingsRepos: SettingsRepos,
    private val dataStore: PreferencesDataStore,
) : ViewModel(), SettingsViewModel {
    private val _appSettings = MutableStateFlow<AppSettings?>(null)
    override val appSettings: StateFlow<AppSettings?> = _appSettings

    init {
        collectSettings()
    }

    override fun sendLogsFile() {
        settingsRepos.sendLogsFile()
    }

    private fun collectSettings() {
        viewModelScope.launch {
            val eventFlow = combine(
                dataStore.groupsVisibilityFlow,
                dataStore.roomsVisibilityFlow,
                dataStore.lecturersVisibilityFlow,
                dataStore.tagVisibilityFlow,
                dataStore.commentVisibilityFlow
            ) { groupsVisibility, roomsVisibility, lecturersVisibility, tagVisibility, commentVisibility ->
                EventView(
                    groupsVisible = groupsVisibility,
                    roomsVisible = roomsVisibility,
                    lecturersVisible = lecturersVisibility,
                    tagVisible = tagVisibility,
                    commentVisible = commentVisibility
                )
            }
            combine(
                dataStore.themeFlow,
                dataStore.decorColorFlow,
                dataStore.scheduleViewFlow,
                dataStore.showCountClassesFlow,
                eventFlow
            ) { theme, decorColorIndex, isCalendarView, isShowCountClasses, eventView ->
                AppSettings(
                    theme = theme,
                    decorColorIndex = decorColorIndex,
                    calendarView = isCalendarView,
                    showCountClasses = isShowCountClasses,
                    eventView = eventView
                )
            }.collect { settings ->
                _appSettings.value = settings
            }
        }
    }

    override fun onSetEventGroupVisibility(visible: Boolean) {
        viewModelScope.launch {
            settingsRepos.onSetEventGroupVisibility(visible)
        }
    }

    override fun onSetEventView(visible: Boolean) {
        viewModelScope.launch {
            settingsRepos.onSetEventView(visible)
        }
    }
    override fun onSetEventRoomsVisibility(visible: Boolean) {
        viewModelScope.launch {
            settingsRepos.onSetEventRoomsVisibility(visible)
        }
    }

    override fun onSetEventLecturersVisibility(visible: Boolean) {
        viewModelScope.launch {
            settingsRepos.onSetEventLecturersVisibility(visible)
        }
    }

    override fun onSetEventTagVisibility(visible: Boolean) {
        viewModelScope.launch {
            settingsRepos.onSetEventTagVisibility(visible)
        }
    }

    override fun onSetEventCommentVisibility(visible: Boolean) {
        viewModelScope.launch {
            settingsRepos.onSetEventCommentVisibility(visible)
        }
    }

    override fun onSetShowCountClasses(
        showCountClasses: Boolean
    ) {
        viewModelScope.launch {
            settingsRepos.onSetShowCountClasses(showCountClasses)
        }
    }

    override fun onSetTheme(
        theme: String
    ) {
        viewModelScope.launch {
            settingsRepos.onSetTheme(theme)
        }
    }

    override fun onSetDecorColor(
        decorColor: Int
    ) {
        viewModelScope.launch {
            settingsRepos.onSetDecorColor(decorColor)
        }
    }

    override fun onSetScheduleView(
        scheduleView: Boolean
    ) {
        viewModelScope.launch {
            settingsRepos.onSetScheduleView(scheduleView)
        }
    }
}