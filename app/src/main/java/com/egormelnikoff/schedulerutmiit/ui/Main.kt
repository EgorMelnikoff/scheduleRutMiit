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
import com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event.AddEventDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomNavigationBar
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSnackbarHost
import com.egormelnikoff.schedulerutmiit.ui.elements.rememberBarItems
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions.Companion.getNavigationActions
import com.egormelnikoff.schedulerutmiit.ui.navigation.Routes
import com.egormelnikoff.schedulerutmiit.ui.screens.news.NewsScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleUiEventProcessor
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleUiStateSynchronizer
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState.Companion.rememberAppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ReviewUiState.Companion.rememberReviewUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState.Companion.rememberScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.state.actions.news.NewsActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.search.SearchActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.settings.SettingsActions
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsViewModel
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun Main(
    searchViewModel: SearchViewModel,
    scheduleViewModel: ScheduleViewModel,
    newsViewModel: NewsViewModel,
    searchActions: SearchActions,
    scheduleActions: ScheduleActions,
    newsActions: NewsActions,
    settingsActions: SettingsActions,
    appSettings: AppSettings
) {
    val appUiState = rememberAppUiState()
    val reviewUiState = rememberReviewUiState()
    val navigationActions = getNavigationActions(
        appBackStack = appUiState.appBackStack
    )

    val scheduleState = scheduleViewModel.scheduleState.collectAsState().value
    val scheduleUiState = rememberScheduleUiState(scheduleState = scheduleState)
    val barItems = rememberBarItems(
        onScheduleClick = {
            appUiState.scope.launch {
                when {
                    scheduleUiState != null && scheduleState.currentNamedScheduleData?.scheduleData?.schedulePagerData != null && appSettings.calendarView -> {
                        scheduleUiState.onSelectDate(
                            scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.defaultDate
                        )
                        scheduleUiState.pagerWeeksState.animateScrollToPage(
                            scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.weeksStartIndex
                        )
                    }

                    scheduleUiState != null && !appSettings.calendarView -> {
                        scheduleUiState.scheduleListState.animateScrollToItem(0)
                    }
                }
            }
        },
        onNewsClick = {
            appUiState.scope.launch {
                appUiState.newsListState.animateScrollToItem(0)
            }
        },
        onSettingsClick = {
            appUiState.scope.launch {
                appUiState.settingsListState.animateScrollToItem(0)
            }
        }
    )
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
                appBackStack = appUiState.appBackStack,
                barItems = barItems,
                theme = appSettings.theme
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
                entry<Routes.Review> {
                    ReviewScreen(
                        scheduleState = scheduleState,
                        reviewUiState = reviewUiState,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.Schedule> {
                    ScreenSchedule(
                        appUiState = appUiState,
                        scheduleState = scheduleState,
                        scheduleUiState = scheduleUiState,
                        appSettings = appSettings,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        settingsActions = settingsActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.NewsList> {
                    NewsScreen(
                        newsListFLow = newsViewModel.newsListFlow,
                        newsGridListState = appUiState.newsListState,
                        navigationActions = navigationActions,
                        newsActions = newsActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.Settings> {
                    SettingsScreen(
                        appUiState = appUiState,
                        settingsListState = appUiState.settingsListState,
                        appSettings = appSettings,
                        navigationActions = navigationActions,
                        settingsActions = settingsActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.EventDialog> { key ->
                    EventDialog(
                        scheduleEntity = key.scheduleEntity,
                        isSavedSchedule = key.isSavedSchedule,
                        isCustomSchedule = key.isCustomSchedule,
                        event = key.event,
                        eventExtraData = key.eventExtraData,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.NewsDialog> {
                    NewsDialog(
                        newsState = newsState,
                        navigationActions = navigationActions,
                        newsActions = newsActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.InfoDialog> {
                    InfoDialog(
                        appUiState = appUiState,
                        navigationActions = navigationActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.AddEventDialog> { key ->
                    AddEventDialog(
                        scheduleEntity = key.scheduleEntity,
                        appUiState = appUiState,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.SearchDialog> {
                    SearchDialog(
                        searchState = searchState,
                        searchParams = searchParams,
                        navigationActions = navigationActions,
                        searchActions = searchActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.AddScheduleDialog> {
                    AddScheduleDialog(
                        appUiState = appUiState,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.RenameNamedScheduleDialog> { key ->
                    RenameDialog(
                        namedScheduleEntity = key.namedScheduleEntity,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.HiddenEventsDialog> { key ->
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