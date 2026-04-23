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
import com.egormelnikoff.schedulerutmiit.ui.WelcomePage
import com.egormelnikoff.schedulerutmiit.ui.theme.ScheduleRutMiitTheme
import com.egormelnikoff.schedulerutmiit.ui.view_models.news.NewsViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.search.SearchViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val scheduleViewModel: ScheduleViewModel by viewModels()
    private val newsViewModel: NewsViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            scheduleViewModel.isDataLoading.value || settingsViewModel.appSettings.value == null
        }

        setContent {
            val appSettings by settingsViewModel.appSettings.collectAsStateWithLifecycle()

            appSettings?.let { settings ->
                ScheduleRutMiitTheme(
                    decorPreferences = settings.decorPreferences
                ) {
                    if (settings.skipWelcomePage) {
                        ScheduleRutMiitApp(
                            searchViewModel = searchViewModel,
                            scheduleViewModel = scheduleViewModel,
                            newsViewModel = newsViewModel,
                            settingsViewModel = settingsViewModel,
                            appSettings = settings
                        )
                    } else {
                        WelcomePage(settingsViewModel)
                    }
                }
            }
        }
    }
}