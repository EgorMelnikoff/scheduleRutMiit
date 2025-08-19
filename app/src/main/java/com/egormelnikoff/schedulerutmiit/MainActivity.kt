package com.egormelnikoff.schedulerutmiit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.data.repos.datastore.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.Main
import com.egormelnikoff.schedulerutmiit.ui.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.news.viewmodel.NewsViewModel
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.search.viewmodel.SearchViewModel
import com.egormelnikoff.schedulerutmiit.ui.settings.viewmodel.SettingsViewModel
import com.egormelnikoff.schedulerutmiit.ui.theme.ScheduleRutMiitTheme
import kotlinx.coroutines.flow.combine

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val container = (applicationContext as ScheduleApplication).container

        val searchViewModel: SearchViewModel by viewModels {
            SearchViewModel.provideFactory(
                container = container
            )
        }
        val scheduleViewModel: ScheduleViewModel by viewModels {
            ScheduleViewModel.provideFactory(
                container = container
            )
        }
        val newsViewModel: NewsViewModel by viewModels {
            NewsViewModel.provideFactory(
                container = container
            )
        }
        val settingsViewModel: SettingsViewModel by viewModels {
            SettingsViewModel.provideFactory(
                container = container
            )
        }

        setContent {
            val appSettings = combine(
                container.dataStore.themeFlow,
                container.dataStore.decorColorFlow,
                container.dataStore.scheduleViewFlow,
                container.dataStore.viewEventFlow,
                container.dataStore.showCountClassesFlow
            ) { theme, primaryColorIndex, isCalendarView, isShortEventView, isShowCountClasses ->
                AppSettings(
                    theme = theme,
                    decorColorIndex = primaryColorIndex,
                    eventView = isShortEventView,
                    calendarView = isCalendarView,
                    showCountClasses = isShowCountClasses,
                )
            }.collectAsState(
                initial = null
            )

            ScheduleRutMiitTheme(
                appSettings = appSettings.value
            ) {
                if (appSettings.value != null) {
                    Main(
                        searchViewModel = searchViewModel,
                        scheduleViewModel = scheduleViewModel,
                        newsViewModel = newsViewModel,
                        settingsViewModel = settingsViewModel,
                        preferencesDataStore = container.dataStore,
                        appSettings = appSettings.value!!
                    )
                } else {
                    LoadingScreen(
                        paddingTop = 0.dp,
                        paddingBottom = 0.dp
                    )
                }
            }
        }
    }
}
