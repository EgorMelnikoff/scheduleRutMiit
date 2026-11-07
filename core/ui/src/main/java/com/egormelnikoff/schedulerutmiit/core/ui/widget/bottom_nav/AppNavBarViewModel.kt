package com.egormelnikoff.schedulerutmiit.core.ui.widget.bottom_nav

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AppNavBarViewModel @Inject constructor(
    private val appRouter: AppRouter,
    private val settingsRepos: SettingsRepos
) : BaseViewModel<AppNavBarState, AppNavBarAction>(AppNavBarState()) {

    override fun onScreenInitialized() {
        super.onScreenInitialized()

        settingsRepos.observeThemePreferences().onEach { themePreferences ->
            updateState {
                copy(
                    theme = themePreferences.theme,
                )
            }
        }
            .launchIn(viewModelScope)

        snapshotFlow { appRouter.lastPage() }
            .distinctUntilChanged()
            .onEach { page ->
                updateState {
                    copy(currentPage = page)
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: AppNavBarAction) {
        when (action) {
            is AppNavBarAction.OnNavigatePage -> {
                appRouter.openPage(action.page)
            }
        }
    }
}