package com.egormelnikoff.schedulerutmiit.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.egormelnikoff.schedulerutmiit.AppContainerInterface
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.AUTHOR_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.data.repos.remote.RemoteReposInterface
import com.egormelnikoff.schedulerutmiit.model.TelegramPage
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AppInfoState {
    data object Loading : AppInfoState
    data class Loaded(
        val authorTelegramPage: TelegramPage? = null,
    ) : AppInfoState
}

class SettingsViewModel(
    private val remoteRepos: RemoteReposInterface
) : ViewModel() {
    companion object {
        fun provideFactory(container: AppContainerInterface): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    return SettingsViewModel(
                        remoteRepos = container.remoteRepos
                    ) as T
                }
            }
        }
    }

    private var infoJob: Job? = null

    private val _stateAppInfo = MutableStateFlow<AppInfoState>(AppInfoState.Loading)
    val stateAppInfo: StateFlow<AppInfoState> = _stateAppInfo


    fun getAppInfo() {
        val newInfoJob = viewModelScope.launch {
            infoJob?.cancelAndJoin()
            _stateAppInfo.value = AppInfoState.Loading
            val authorInfo = remoteRepos.getTgChannelInfo(AUTHOR_CHANNEL_URL)

            _stateAppInfo.value = AppInfoState.Loaded(
                authorTelegramPage = when (authorInfo) {
                    is Result.Error -> null
                    is Result.Success -> authorInfo.data
                }
            )
        }
        infoJob = newInfoJob
    }
}