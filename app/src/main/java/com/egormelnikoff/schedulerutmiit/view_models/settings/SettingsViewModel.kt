package com.egormelnikoff.schedulerutmiit.view_models.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.app.AppConst.AUTHOR_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.app.model.TelegramPage
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.EventView
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.datastore.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.repos.settings.SettingsRepos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SettingsViewModel {
    val settingsState: StateFlow<SettingsState>
    val appSettings: StateFlow<AppSettings?>
    fun getAppInfo()
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

sealed interface SettingsState {
    data object Loading : SettingsState
    data class Loaded(
        val authorTelegramPage: TelegramPage? = null,
    ) : SettingsState
}

@HiltViewModel
class SettingsViewModelImpl @Inject constructor(
    private val settingsRepos: SettingsRepos,
    private val dataStore: PreferencesDataStore
) : ViewModel(), SettingsViewModel {
    private val _settingsState = MutableStateFlow<SettingsState>(SettingsState.Loading)
    override val settingsState: StateFlow<SettingsState> = _settingsState

    private val _appSettings = MutableStateFlow<AppSettings?>(null)
    override val appSettings: StateFlow<AppSettings?> = _appSettings

    private var infoJob: Job? = null

    init {
        collectSettings()
    }

    override fun getAppInfo() {
        val newInfoJob = viewModelScope.launch {
            infoJob?.cancelAndJoin()
            _settingsState.value = SettingsState.Loading
            val authorInfo = settingsRepos.getTgChannelInfo(AUTHOR_CHANNEL_URL)

            _settingsState.value = SettingsState.Loaded(
                authorTelegramPage = when (authorInfo) {
                    is Result.Error -> null
                    is Result.Success -> authorInfo.data
                }
            )
        }
        infoJob = newInfoJob
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
            dataStore.setEventGroupVisibility(visible)
        }
    }

    override fun onSetEventView(visible: Boolean) {
        viewModelScope.launch {
            dataStore.setEventGroupVisibility(visible)
            dataStore.setEventRoomsVisibility(visible)
            dataStore.setEventLecturersVisibility(visible)
            dataStore.setEventTagVisibility(visible)
            dataStore.setEventCommentVisibility(visible)
        }
    }
    override fun onSetEventRoomsVisibility(visible: Boolean) {
        viewModelScope.launch {
            dataStore.setEventRoomsVisibility(visible)
        }
    }

    override fun onSetEventLecturersVisibility(visible: Boolean) {
        viewModelScope.launch {
            dataStore.setEventLecturersVisibility(visible)
        }
    }

    override fun onSetEventTagVisibility(visible: Boolean) {
        viewModelScope.launch {
            dataStore.setEventTagVisibility(visible)
        }
    }

    override fun onSetEventCommentVisibility(visible: Boolean) {
        viewModelScope.launch {
            dataStore.setEventCommentVisibility(visible)
        }
    }

    override fun onSetShowCountClasses(
        showCountClasses: Boolean
    ) {
        viewModelScope.launch {
            dataStore.setShowCountClasses(showCountClasses)
        }
    }

    override fun onSetTheme(
        theme: String
    ) {
        viewModelScope.launch {
            dataStore.setTheme(theme)
        }
    }

    override fun onSetDecorColor(
        decorColor: Int
    ) {
        viewModelScope.launch {
            dataStore.setDecorColor(decorColor)
        }
    }

    override fun onSetScheduleView(
        scheduleView: Boolean
    ) {
        viewModelScope.launch {
            dataStore.setScheduleView(scheduleView)
        }
    }
}