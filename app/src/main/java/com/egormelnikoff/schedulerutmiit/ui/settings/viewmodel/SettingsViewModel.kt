package com.egormelnikoff.schedulerutmiit.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.egormelnikoff.schedulerutmiit.AppContainer
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.AppSettings
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.AUTHOR_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.data.repos.Repos
import com.egormelnikoff.schedulerutmiit.model.TelegramPage
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

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


class SettingsViewModelImpl(
    private val repos: Repos,
    private val dataStore: DataStore
) : ViewModel(), SettingsViewModel {
    companion object {
        fun provideFactory(container: AppContainer): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    return SettingsViewModelImpl(
                        repos = container.repos,
                        dataStore = container.dataStore
                    ) as T
                }
            }
        }
    }

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
            val authorInfo = repos.getTgChannelInfo(AUTHOR_CHANNEL_URL)

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