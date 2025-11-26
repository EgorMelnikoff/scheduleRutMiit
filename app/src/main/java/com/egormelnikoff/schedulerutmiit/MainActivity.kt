package com.egormelnikoff.schedulerutmiit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.ui.Main
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions.Companion.getScheduleActions
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

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            scheduleViewModel.isDataLoading.value || settingsViewModel.appSettings.value == null
        }

        val scheduleActions = getScheduleActions(
            scheduleViewModel = scheduleViewModel
        )

        setContent {
            val appSettings by settingsViewModel.appSettings.collectAsStateWithLifecycle()

            appSettings?.let { settings ->
                ScheduleRutMiitTheme(appSettings = settings) {
                    Main(
                        searchViewModel = searchViewModel,
                        scheduleViewModel = scheduleViewModel,
                        newsViewModel = newsViewModel,
                        settingsViewModel = settingsViewModel,
                        scheduleActions = scheduleActions,
                        appSettings = settings
                    )
                }
            }
        }
    }
}

