package com.egormelnikoff.schedulerutmiit.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.dialogs.AddScheduleDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.EventDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.HiddenEventsDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.InfoDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.NewsDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.RenameDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.SearchDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event.AddEditEventDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomNavigationBar
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSnackbarHost
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions.Companion.getNavigationActions
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.ui.screens.news.NewsScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleUiEventProcessor
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleUiStateSynchronizer
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState.Companion.rememberAppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ReviewUiState.Companion.rememberReviewUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState.Companion.rememberScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsViewModel
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModel
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel

@Composable
fun Main(
    searchViewModel: SearchViewModel,
    scheduleViewModel: ScheduleViewModel,
    newsViewModel: NewsViewModel,
    settingsViewModel: SettingsViewModel,
    scheduleActions: ScheduleActions,
    appSettings: AppSettings
) {
    val appUiState = rememberAppUiState()
    val reviewUiState = rememberReviewUiState()
    val navigationActions = getNavigationActions(
        appBackStack = appUiState.appBackStack
    )

    val scheduleState = scheduleViewModel.scheduleState.collectAsState().value
    val scheduleUiState = rememberScheduleUiState(scheduleState = scheduleState)

    val searchState = searchViewModel.searchState.collectAsState().value
    val searchParams = searchViewModel.searchParams.collectAsState().value
    val newsState = newsViewModel.newsState.collectAsState().value

    ScheduleUiEventProcessor(
        scheduleViewModel = scheduleViewModel,
        snackBarHostState = appUiState.snackBarHostState
    )

    ScheduleUiStateSynchronizer(
        scheduleUiState = scheduleUiState,
        scheduleState = scheduleState
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomNavigationBar(
                appUiState = appUiState,
                scheduleUiState = scheduleUiState,
                scheduleState = scheduleState,
                appSettings = appSettings
            )
        },
        snackbarHost = {
            CustomSnackbarHost(
                snackBarHostState = appUiState.snackBarHostState
            )
        }
    ) { externalPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                ),
            backStack = appUiState.appBackStack.backStack,
            onBack = {
                appUiState.appBackStack.onBack()
            },
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            popTransitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            predictivePopTransitionSpec = {
                when (it) {
                    0 -> {
                        fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                    }

                    1 -> {
                        fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth })
                    }

                    else -> {
                        fadeIn() togetherWith fadeOut()
                    }
                }
            },
            entryProvider = entryProvider {
                entry<Route.Page.Review> {
                    ReviewScreen(
                        scheduleState = scheduleState,
                        reviewUiState = reviewUiState,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Route.Page.Schedule> {
                    ScreenSchedule(
                        appUiState = appUiState,
                        scheduleState = scheduleState,
                        scheduleUiState = scheduleUiState,
                        appSettings = appSettings,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        settingsViewModel = settingsViewModel,
                        externalPadding = externalPadding
                    )
                }

                entry<Route.Page.NewsList> {
                    NewsScreen(
                        newsListFLow = newsViewModel.newsListFlow,
                        newsGridListState = appUiState.newsListState,
                        newsViewModel = newsViewModel,
                        navigationActions = navigationActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Route.Page.Settings> {
                    SettingsScreen(
                        appUiState = appUiState,
                        settingsListState = appUiState.settingsListState,
                        appSettings = appSettings,
                        navigationActions = navigationActions,
                        settingsViewModel = settingsViewModel,
                        externalPadding = externalPadding
                    )
                }

                entry<Route.Dialog.EventDialog> { key ->
                    EventDialog(
                        scheduleEntity = key.scheduleEntity,
                        isSavedSchedule = key.isSavedSchedule,
                        event = key.event,
                        eventExtraData = key.eventExtraData,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Route.Dialog.NewsDialog> {
                    NewsDialog(
                        newsState = newsState,
                        navigationActions = navigationActions,
                        newsViewModel = newsViewModel,
                        externalPadding = externalPadding
                    )
                }

                entry<Route.Dialog.InfoDialog> {
                    InfoDialog(
                        appUiState = appUiState,
                        navigationActions = navigationActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Route.Dialog.AddEventDialog> { key ->
                    AddEditEventDialog(
                        scheduleEntity = key.scheduleEntity,
                        editableEvent = key.event,
                        appUiState = appUiState,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Route.Dialog.SearchDialog> {
                    SearchDialog(
                        searchState = searchState,
                        searchParams = searchParams,
                        navigationActions = navigationActions,
                        searchViewModel = searchViewModel,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Route.Dialog.AddScheduleDialog> {
                    AddScheduleDialog(
                        appUiState = appUiState,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Route.Dialog.RenameNamedScheduleDialog> { key ->
                    RenameDialog(
                        namedScheduleEntity = key.namedScheduleEntity,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Route.Dialog.HiddenEventsDialog> { key ->
                    HiddenEventsDialog(
                        scheduleEntity = key.scheduleEntity,
                        hiddenEvents = scheduleState.currentNamedScheduleData!!.scheduleData!!.hiddenEvents,
                        navigationActions = navigationActions,
                        eventActions = scheduleActions.eventActions,
                        externalPadding = externalPadding
                    )
                }
            }
        )
    }
}