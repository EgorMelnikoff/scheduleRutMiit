package com.egormelnikoff.schedulerutmiit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.ui.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.SearchViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.SettingsViewModel
import com.egormelnikoff.schedulerutmiit.ui.theme.ScheduleRutMiitTheme
import com.egormelnikoff.schedulerutmiit.ui.view_models.NewsViewModel
import kotlinx.coroutines.flow.combine

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val preferencesDataStore = DataStore(this)
        val searchViewModel = SearchViewModel()
        val scheduleViewModel = ScheduleViewModel(this)
        val settingsViewModel = SettingsViewModel()
        val newsViewModel = NewsViewModel()

        setContent {
            val appSettings = combine(
                preferencesDataStore.themeFlow,
                preferencesDataStore.decorColorFlow,
                preferencesDataStore.scheduleViewFlow,
                preferencesDataStore.viewEventFlow,
                preferencesDataStore.showCountClassesFlow
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
                        scheduleViewModel = scheduleViewModel,
                        searchViewModel = searchViewModel,
                        settingsViewModel = settingsViewModel,
                        newsViewModel = newsViewModel,
                        preferencesDataStore = preferencesDataStore,
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
