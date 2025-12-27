package com.egormelnikoff.schedulerutmiit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.ui.ScheduleRutMiitApp
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions.Companion.scheduleActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.search.SearchActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.search.SearchActions.Companion.searchActions
import com.egormelnikoff.schedulerutmiit.ui.theme.ScheduleRutMiitTheme
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsViewModelImpl
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModelImpl
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModelImpl
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val scheduleViewModel: ScheduleViewModelImpl by viewModels()
    private val searchViewModel: SearchViewModelImpl by viewModels()
    private val newsViewModel: NewsViewModelImpl by viewModels()
    private val settingsViewModel: SettingsViewModelImpl by viewModels()

    private lateinit var scheduleActions: ScheduleActions
    private lateinit var searchActions: SearchActions

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            scheduleViewModel.isDataLoading.value || settingsViewModel.appSettings.value == null
        }

        scheduleActions = scheduleActions(
            scheduleViewModel = scheduleViewModel
        )

        searchActions = searchActions(
            searchViewModel = searchViewModel
        )

        setContent {
            val appSettings by settingsViewModel.appSettings.collectAsStateWithLifecycle()

            appSettings?.let { settings ->
                ScheduleRutMiitTheme(
                    theme = settings.theme,
                    decorColorIndex = settings.decorColorIndex
                ) {
                    ScheduleRutMiitApp(
                        searchViewModel = searchViewModel,
                        scheduleViewModel = scheduleViewModel,
                        newsViewModel = newsViewModel,
                        settingsViewModel = settingsViewModel,
                        scheduleActions = scheduleActions,
                        searchActions = searchActions,
                        appSettings = settings
                    )
                }
            }
        }
    }
}