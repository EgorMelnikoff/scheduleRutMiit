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
import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.dialogs.AddEventDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.AddScheduleDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.EventDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.HiddenEventsDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.InfoDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.NewsDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.RenameDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.SearchDialog
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
import com.egormelnikoff.schedulerutmiit.ui.state.actions.news.NewsActions.Companion.getNewsActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions.Companion.getScheduleActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.search.SearchActions.Companion.getSearchActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.settings.SettingsActions.Companion.getSettingsActions
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsViewModel
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModel
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun Main(
    searchViewModel: SearchViewModel,
    scheduleViewModel: ScheduleViewModel,
    newsViewModel: NewsViewModel,
    settingsViewModel: SettingsViewModel,
    appSettings: AppSettings,
    logger: Logger
) {
    val searchState = searchViewModel.searchState.collectAsState().value
    val scheduleState = scheduleViewModel.scheduleState.collectAsState().value
    val newsState = newsViewModel.newsState.collectAsState().value
    val settingsState = settingsViewModel.settingsState.collectAsState().value

    val appUiState = rememberAppUiState()
    val scheduleUiState = rememberScheduleUiState(scheduleState = scheduleState)
    val reviewUiState = rememberReviewUiState()

    val navigationActions = getNavigationActions(
        appBackStack = appUiState.appBackStack
    )
    val searchActions = getSearchActions(
        searchViewModel = searchViewModel,
        reviewUiState = reviewUiState
    )
    val scheduleActions = getScheduleActions(
        scheduleViewModel = scheduleViewModel,
        scheduleState = scheduleState,
        appUiState = appUiState
    )
    val newsActions = getNewsActions(
        newsViewModel = newsViewModel
    )
    val settingsActions = getSettingsActions(
        settingsViewModel = settingsViewModel,
        settingsState = settingsState,
        appUiState = appUiState,
        logger = logger
    )

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
                barItems = rememberBarItems(
                    onScheduleClick = {
                        appUiState.scope.launch {
                            when {
                                scheduleUiState != null && scheduleState.currentNamedScheduleData!!.schedulePagerData != null && appSettings.calendarView -> {
                                    scheduleUiState.onSelectDate(
                                        scheduleState.currentNamedScheduleData.schedulePagerData.defaultDate
                                    )
                                    scheduleUiState.pagerWeeksState.animateScrollToPage(
                                        scheduleState.currentNamedScheduleData.schedulePagerData.weeksStartIndex
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
                ),
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
                        newsState = newsState,
                        newsListFLow = newsViewModel.newsListFlow,
                        newsGridListState = appUiState.newsListState,
                        navigationActions = navigationActions,
                        newsActions = newsActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.Settings> {
                    SettingsScreen(
                        settingsListState = appUiState.settingsListState,
                        appSettings = appSettings,
                        navigationActions = navigationActions,
                        settingsActions = settingsActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.EventDialog> { key ->
                    EventDialog(
                        event = key.event,
                        eventExtraData = key.eventExtraData,
                        isSavedSchedule = scheduleState.isSaved,
                        isCustomSchedule = scheduleState.currentNamedScheduleData!!.namedSchedule!!.namedScheduleEntity.type == 3,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.NewsDialog> {
                    NewsDialog(
                        newsState = newsState,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.InfoDialog> {
                    InfoDialog(
                        settingsState = settingsState,
                        settingsActions = settingsActions,
                        navigationActions = navigationActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.AddEventDialog> {
                    AddEventDialog(
                        scheduleEntity = scheduleState.currentNamedScheduleData!!.settledScheduleEntity!!,
                        focusManager = appUiState.focusManager,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.SearchDialog> {
                    SearchDialog(
                        searchState = searchState,
                        reviewUiState = reviewUiState,
                        navigationActions = navigationActions,
                        searchActions = searchActions,
                        scheduleActions = scheduleActions,
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.AddScheduleDialog> {
                    AddScheduleDialog(
                        focusManager = appUiState.focusManager,
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

                entry<Routes.HiddenEventsDialog> {
                    HiddenEventsDialog(
                        hiddenEvents = scheduleState.currentNamedScheduleData!!.hiddenEvents,
                        eventsExtraData = scheduleState.currentNamedScheduleData.eventsExtraData,
                        navigationActions = navigationActions,
                        eventActions = scheduleActions.eventActions,
                        externalPadding = externalPadding
                    )
                }
            }
        )
    }
}