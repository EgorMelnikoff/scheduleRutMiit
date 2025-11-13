package com.egormelnikoff.schedulerutmiit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.ui.Main
import com.egormelnikoff.schedulerutmiit.ui.theme.ScheduleRutMiitTheme
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsViewModelImpl
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModelImpl
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModelImpl
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val scheduleViewModel: ScheduleViewModelImpl by viewModels()
    private val searchViewModel: SearchViewModelImpl by viewModels()
    private val newsViewModel: NewsViewModelImpl by viewModels()
    private val settingsViewModel: SettingsViewModelImpl by viewModels()

    @Inject
    lateinit var logger: Logger

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
                ScheduleRutMiitTheme(appSettings = settings) {
                    Main(
                        searchViewModel = searchViewModel,
                        scheduleViewModel = scheduleViewModel,
                        newsViewModel = newsViewModel,
                        settingsViewModel = settingsViewModel,
                        logger = logger,
                        appSettings = settings
                    )
                }
            }
        }
    }
}

