package com.egormelnikoff.schedulerutmiit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.ui.Main
import com.egormelnikoff.schedulerutmiit.ui.news.viewmodel.NewsViewModelImpl
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleViewModelImpl
import com.egormelnikoff.schedulerutmiit.ui.search.viewmodel.SearchViewModelImpl
import com.egormelnikoff.schedulerutmiit.ui.settings.viewmodel.SettingsViewModelImpl
import com.egormelnikoff.schedulerutmiit.ui.theme.ScheduleRutMiitTheme


class MainActivity : ComponentActivity() {
    private lateinit var container: AppContainer
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        container = (applicationContext as ScheduleApplication).container

        val searchViewModel: SearchViewModelImpl by viewModels {
            SearchViewModelImpl.provideFactory(
                container = container
            )
        }
        val scheduleViewModel: ScheduleViewModelImpl by viewModels {
            ScheduleViewModelImpl.provideFactory(
                container = container
            )
        }
        val newsViewModel: NewsViewModelImpl by viewModels {
            NewsViewModelImpl.provideFactory(
                container = container
            )
        }
        val settingsViewModel: SettingsViewModelImpl by viewModels {
            SettingsViewModelImpl.provideFactory(
                container = container
            )
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
                        preferencesDataStore = container.dataStore,
                        appSettings = settings
                    )
                }
            }
        }
    }
}

