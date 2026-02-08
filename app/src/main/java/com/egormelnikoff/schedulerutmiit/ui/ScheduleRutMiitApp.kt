package com.egormelnikoff.schedulerutmiit.ui

import android.annotation.SuppressLint
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.dialogs.AddScheduleDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.CurriculumDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.EventDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.HiddenEventsDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.InfoDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.NewsDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.RenameDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.SearchDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event.AddEditEventDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomNavigationBar
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSnackbarHost
import com.egormelnikoff.schedulerutmiit.ui.elements.barItems
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.ui.screens.news.NewsScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleUiEventProcessor
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleUiStateSynchronizer
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleView
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ReviewUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.state.actions.ScheduleActions
import com.egormelnikoff.schedulerutmiit.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.view_models.curriculum.CurriculumViewModel
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsState
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsViewModel
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchParams
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchState
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModel
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun ScheduleRutMiitApp(
    scheduleViewModel: ScheduleViewModel,
    searchViewModel: SearchViewModel,
    newsViewModel: NewsViewModel,
    settingsViewModel: SettingsViewModel,
    scheduleActions: ScheduleActions,
    appSettings: AppSettings
) {
    val searchParams = searchViewModel.searchParams.collectAsStateWithLifecycle().value
    val searchState = searchViewModel.searchState.collectAsStateWithLifecycle().value
    val scheduleState = scheduleViewModel.scheduleState.collectAsStateWithLifecycle().value
    val newsState = newsViewModel.newsState.collectAsStateWithLifecycle().value

    val appUiState = AppUiState()
    val navigationActions = NavigationActions(appUiState.appBackStack)
    val scheduleUiState = ScheduleUiState(scheduleState)
    val reviewUiState = ReviewUiState()

    ScheduleUiEventProcessor(
        scheduleViewModel = scheduleViewModel,
        snackBarHostState = appUiState.snackBarHostState
    )

    ScheduleUiStateSynchronizer(
        scheduleUiState = scheduleUiState,
        scheduleState = scheduleState
    )

    Box(Modifier.fillMaxSize()) {
        RootHost(
            pageHost = {
                PageHost(
                    appUiState = appUiState,
                    newsViewModel = newsViewModel,
                    settingsViewModel = settingsViewModel,
                    scheduleActions = scheduleActions,
                    scheduleState = scheduleState,
                    scheduleUiState = scheduleUiState,
                    reviewUiState = reviewUiState,
                    appSettings = appSettings,
                    navigationActions = navigationActions
                )
            },
            navigationActions = navigationActions,
            scheduleActions = scheduleActions,
            newsViewModel = newsViewModel,
            searchViewModel = searchViewModel,

            appUiState = appUiState,
            scheduleState = scheduleState,
            newsState = newsState,
            searchState = searchState,
            searchParams = searchParams
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RootHost(
    pageHost: @Composable () -> Unit,
    navigationActions: NavigationActions,
    scheduleActions: ScheduleActions,
    searchViewModel: SearchViewModel,
    newsViewModel: NewsViewModel,

    appUiState: AppUiState,
    searchState: SearchState,
    searchParams: SearchParams,
    scheduleState: ScheduleState,
    newsState: NewsState
) {
    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(
                snackBarHostState = appUiState.snackBarHostState
            )
        }
    ) {
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                ),
            backStack = appUiState.appBackStack.dialogBackStack,
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
                entry<Route.Dialog.Empty> {
                    pageHost.invoke()
                }

                entry<Route.Dialog.EventDialog> { key ->
                    EventDialog(
                        scheduleEntity = key.scheduleEntity,
                        isSavedSchedule = key.isSavedSchedule,
                        event = key.event,
                        eventExtraData = key.eventExtraData,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        searchViewModel = searchViewModel,
                        appUiState = appUiState
                    )
                }

                entry<Route.Dialog.NewsDialog> {
                    NewsDialog(
                        setDefaultState = {
                            newsViewModel.setDefaultNewsState()
                        },
                        newsState = newsState,
                        navigationActions = navigationActions
                    )
                }
                entry<Route.Dialog.InfoDialog> {
                    InfoDialog(
                        appUiState = appUiState,
                        navigationActions = navigationActions
                    )
                }
                entry<Route.Dialog.AddEventDialog> { key ->
                    AddEditEventDialog(
                        scheduleEntity = key.scheduleEntity,
                        editableEvent = key.event,
                        appUiState = appUiState,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions
                    )
                }
                entry<Route.Dialog.SearchDialog> {
                    SearchDialog(
                        searchViewModel = searchViewModel,
                        searchParams = searchParams,
                        searchState = searchState,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions
                    )
                }
                entry<Route.Dialog.CurriculumDialog> {
                    val curriculumViewModel = hiltViewModel<CurriculumViewModel>()
                    val searchQuery =
                        curriculumViewModel.searchQuery.collectAsStateWithLifecycle().value
                    val curriculumState =
                        curriculumViewModel.curriculumState.collectAsStateWithLifecycle().value


                    CurriculumDialog(
                        curriculumViewModel = curriculumViewModel,
                        searchQuery = searchQuery,
                        curriculumState = curriculumState
                    )
                }
                entry<Route.Dialog.AddScheduleDialog> {
                    AddScheduleDialog(
                        appUiState = appUiState,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions
                    )
                }
                entry<Route.Dialog.RenameNamedScheduleDialog> { key ->
                    RenameDialog(
                        namedScheduleEntity = key.namedScheduleEntity,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions
                    )
                }
                entry<Route.Dialog.HiddenEventsDialog> { key ->
                    HiddenEventsDialog(
                        scheduleEntity = key.scheduleEntity,
                        hiddenEvents = scheduleState.currentNamedScheduleData!!.scheduleData!!.hiddenEvents,
                        navigationActions = navigationActions,
                        eventActions = scheduleActions.eventActions
                    )
                }
            }
        )
    }
}

@Composable
fun PageHost(
    navigationActions: NavigationActions,
    scheduleActions: ScheduleActions,
    newsViewModel: NewsViewModel,
    settingsViewModel: SettingsViewModel,

    scheduleState: ScheduleState,

    appUiState: AppUiState,
    scheduleUiState: ScheduleUiState?,
    reviewUiState: ReviewUiState,

    appSettings: AppSettings
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomNavigationBar(
                appBackStack = appUiState.appBackStack,
                barItems = barItems(
                    onScheduleClick = {
                        appUiState.scope.launch {
                            when {
                                scheduleUiState != null && scheduleState.currentNamedScheduleData?.scheduleData?.schedulePagerData != null && appSettings.scheduleView == ScheduleView.CALENDAR -> {
                                    scheduleUiState.onSelectDate(
                                        scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.defaultDate
                                    )
                                    scheduleUiState.pagerWeeksState.animateScrollToPage(
                                        scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.weeksStartIndex
                                    )
                                }

                                scheduleUiState != null && appSettings.scheduleView == ScheduleView.LIST -> {
                                    scheduleUiState.scheduleListState.animateScrollToItem(0)
                                }

                                scheduleUiState != null && scheduleState.currentNamedScheduleData?.scheduleData?.schedulePagerData != null && appSettings.scheduleView == ScheduleView.SPLIT_WEEKS -> {
                                    scheduleUiState.pagerSplitWeeks.scrollToPage(
                                        scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.defaultDate.dayOfWeek.value - 1
                                    )
                                    scheduleUiState.onSelectWeek(
                                        scheduleState.currentNamedScheduleData.scheduleData.scheduleEntity?.recurrence?.currentNumber
                                            ?: 0
                                    )
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
                isDarkTheme = appSettings.theme.isDarkTheme()
            )
        }
    ) { padding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                ),
            backStack = appUiState.appBackStack.pageBackStack,
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
                fadeIn() togetherWith fadeOut()
            },
            entryProvider = entryProvider {
                entry<Route.Page.Review> {
                    ReviewScreen(
                        scheduleState = scheduleState,
                        reviewUiState = reviewUiState,
                        navigationActions = navigationActions,
                        scheduleActions = scheduleActions,
                        externalPadding = padding
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
                        externalPadding = padding
                    )
                }

                entry<Route.Page.NewsList> {
                    NewsScreen(
                        newsListFlow = newsViewModel.newsListFlow,
                        onGetNewsById = { id ->
                            newsViewModel.getNewsById(id)
                        },
                        newsGridListState = appUiState.newsListState,
                        navigationActions = navigationActions,
                        externalPadding = padding
                    )
                }

                entry<Route.Page.Settings> {
                    SettingsScreen(
                        appUiState = appUiState,
                        appSettings = appSettings,
                        navigationActions = navigationActions,
                        settingsViewModel = settingsViewModel,
                        externalPadding = padding
                    )
                }
            }
        )
    }
}