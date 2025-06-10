package com.egormelnikoff.schedulerutmiit.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.classes.TelegramPage
import com.egormelnikoff.schedulerutmiit.data.repos.Result
import com.egormelnikoff.schedulerutmiit.data.repos.remote.RemoteRepos
import com.egormelnikoff.schedulerutmiit.data.repos.remote.parser.ParserRoutes.AUTHOR_CHANNEL_URL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed interface AppInfoState {
    data object Loading : AppInfoState
    data class Loaded(
        val authorTelegramPage: TelegramPage? = null,
    ) : AppInfoState
}

class SettingsViewModel : ViewModel() {
    private val _stateAppInfo = MutableStateFlow<AppInfoState>(AppInfoState.Loading)
    val stateAuthor: StateFlow<AppInfoState> = _stateAppInfo

    fun getInfo() {
        viewModelScope.launch {
            _stateAppInfo.value = AppInfoState.Loading
            val authorInfo = RemoteRepos.getTgChannelInfo(AUTHOR_CHANNEL_URL)

            _stateAppInfo.value = AppInfoState.Loaded(
                authorTelegramPage = when (authorInfo) {
                    is Result.Error -> null
                    is Result.Success -> authorInfo.data
                }
            )
        }
    }
}