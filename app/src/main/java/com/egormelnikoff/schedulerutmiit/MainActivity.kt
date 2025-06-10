package com.egormelnikoff.schedulerutmiit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.SearchViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.SettingsViewModel
import com.egormelnikoff.schedulerutmiit.ui.theme.ScheduleRutMiitTheme
import kotlinx.coroutines.flow.combine

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesDataStore = DataStore(this)
        val searchViewModel = SearchViewModel()
        val scheduleViewModel = ScheduleViewModel(this)
        val settingsViewModel = SettingsViewModel()

        setContent {
            val flowBasic = combine(
                preferencesDataStore.themeFlow,
                preferencesDataStore.decorColorFlow,
                preferencesDataStore.scheduleViewFlow
            ) { theme, primaryColorIndex, isCalendarView ->
                Triple(theme, primaryColorIndex, isCalendarView)
            }

            val flowSchedule = combine(
                preferencesDataStore.viewEventFlow,
                preferencesDataStore.showTagsFlow,
                preferencesDataStore.showCountClassesFlow,
            ) { isShortEventView, isShowPriorities, isShowCountClasses ->
                Triple(isShortEventView, isShowPriorities, isShowCountClasses)
            }

            val appSettings = combine(
                flowSchedule,
                flowBasic
            ) { scheduleFlow, basicFlow ->
                AppSettings(
                    theme = basicFlow.first,
                    decorColorIndex = basicFlow.second,
                    eventView = scheduleFlow.first,
                    calendarView = basicFlow.third,
                    showTags = scheduleFlow.second,
                    showCountClasses = scheduleFlow.third,
                )
            }.collectAsState(
                initial = AppSettings(
                    theme = "system",
                    decorColorIndex = 0,
                    eventView = false,
                    calendarView = true,
                    showTags = true,
                    showCountClasses = true
                )
            )


            ScheduleRutMiitTheme(
                appSettings = appSettings.value
            ) {
                Main(
                    scheduleViewModel = scheduleViewModel,
                    searchViewModel = searchViewModel,
                    settingsViewModel = settingsViewModel,
                    preferencesDataStore = preferencesDataStore,
                    appSettings = appSettings.value
                )
            }
        }
    }
}
