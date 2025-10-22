package com.egormelnikoff.schedulerutmiit.view_models.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.app.AppConst.AUTHOR_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.app.model.TelegramPage
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
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
    val stateAppInfo: StateFlow<AppInfoState>
    val appSettings: StateFlow<AppSettings?>
    fun getAppInfo()
}

sealed interface AppInfoState {
    data object Loading : AppInfoState
    data class Loaded(
        val authorTelegramPage: TelegramPage? = null,
    ) : AppInfoState
}

@HiltViewModel
class SettingsViewModelImpl @Inject constructor(
    private val settingsRepos: SettingsRepos,
    private val dataStore: PreferencesDataStore
) : ViewModel(), SettingsViewModel {
    private val _stateAppInfo = MutableStateFlow<AppInfoState>(AppInfoState.Loading)
    private val _appSettings = MutableStateFlow<AppSettings?>(null)

    override val stateAppInfo: StateFlow<AppInfoState> = _stateAppInfo
    override val appSettings: StateFlow<AppSettings?> = _appSettings

    private var infoJob: Job? = null

    init {
        getAppInfo()
        collectSettings()
    }

    override fun getAppInfo() {
        val newInfoJob = viewModelScope.launch {
            infoJob?.cancelAndJoin()
            _stateAppInfo.value = AppInfoState.Loading
            val authorInfo = settingsRepos.getTgChannelInfo(AUTHOR_CHANNEL_URL)

            _stateAppInfo.value = AppInfoState.Loaded(
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
            combine(
                dataStore.themeFlow,
                dataStore.decorColorFlow,
                dataStore.scheduleViewFlow,
                dataStore.viewEventFlow,
                dataStore.showCountClassesFlow
            ) { theme, primaryColorIndex, isCalendarView, isShortEventView, isShowCountClasses ->
                AppSettings(
                    theme = theme,
                    decorColorIndex = primaryColorIndex,
                    eventView = isShortEventView,
                    calendarView = isCalendarView,
                    showCountClasses = isShowCountClasses,
                )
            }.collect { settings ->
                _appSettings.value = settings
            }
        }
    }
}