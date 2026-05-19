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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomSnackbarHost
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.CurriculumDialog
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.view_model.CurriculumViewModel
import com.egormelnikoff.schedulerutmiit.news.ui.NewsDialog
import com.egormelnikoff.schedulerutmiit.news.ui.NewsScreen
import com.egormelnikoff.schedulerutmiit.news.view_model.NewsListViewModel
import com.egormelnikoff.schedulerutmiit.news.view_model.NewsViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.AddEditEventDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.AddScheduleDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.EventDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.HiddenEventsDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.RenameDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.ScheduleUiStateSynchronizer
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.AppUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.ReviewUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.CurrentState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.search.ui.view_model.SearchViewModel
import com.egormelnikoff.schedulerutmiit.ui.screens.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.UiEventProcessor
import com.egormelnikoff.schedulerutmiit.ui.screens.search.SearchDialog
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.view_model.MainViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_model.PreferencesViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_model.state.AppState
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun ScheduleRutMiitApp(
    scheduleViewModel: ScheduleViewModel,
    preferencesViewModel: PreferencesViewModel,
    appSettings: AppSettings
) {
    val mainViewModel = hiltViewModel<MainViewModel>()

    val currentState = scheduleViewModel.currentState.collectAsStateWithLifecycle().value
    val namedScheduleState = scheduleViewModel.namedScheduleState.collectAsStateWithLifecycle().value
    val scheduleState = scheduleViewModel.scheduleState.collectAsStateWithLifecycle().value

    val currentDateTime = mainViewModel.currentDate.collectAsStateWithLifecycle().value
    val appState = mainViewModel.appState.collectAsStateWithLifecycle().value

    val appUiState = AppUiState()
    val scheduleUiState = ScheduleUiState(namedScheduleState, scheduleState)
    val reviewUiState = ReviewUiState()

    UiEventProcessor(
        scheduleViewModel = scheduleViewModel,
        mainViewModel = mainViewModel,
        snackBarHostState = appUiState.snackBarHostState
    )

    ScheduleUiStateSynchronizer(
        scheduleUiState = scheduleUiState,
        currentState = currentState,
        scheduleState = scheduleState,
        namedScheduleState = namedScheduleState,
        scheduleViewModel = scheduleViewModel,
        currentDateTime = currentDateTime
    )

    Box(Modifier.fillMaxSize()) {
        RootHost(
            pageHost = {
                PageHost(
                    appUiState = appUiState,
                    mainViewModel = mainViewModel,
                    scheduleViewModel = scheduleViewModel,
                    currentDateTime = currentDateTime,
                    preferencesViewModel = preferencesViewModel,
                    scheduleState = scheduleState,
                    currentState = currentState,
                    namedScheduleState = namedScheduleState,
                    appState = appState,
                    scheduleUiState = scheduleUiState,
                    reviewUiState = reviewUiState,
                    appSettings = appSettings
                )
            },
            scheduleViewModel = scheduleViewModel,

            appUiState = appUiState,
            scheduleState = scheduleState,
            currentDateTime = currentDateTime
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RootHost(
    pageHost: @Composable () -> Unit,
    scheduleViewModel: ScheduleViewModel,
    appUiState: AppUiState,
    scheduleState: ScheduleState,
    currentDateTime: LocalDateTime
) {
    Scaffold(
        snackbarHost = {
            if (appUiState.appBackStack.dialogBackStack.size > 1) {
                CustomSnackbarHost(
                    snackBarHostState = appUiState.snackBarHostState
                )
            }
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
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
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
                        namedSchedule = key.namedSchedule,
                        schedule = key.schedule,
                        isSavedSchedule = key.isSavedSchedule,
                        dateTime = key.dateTime,
                        event = key.event,
                        eventExtraData = key.eventExtraData,
                        appBackStack = appUiState.appBackStack,
                        scheduleViewModel = scheduleViewModel
                    )
                }

                entry<Route.Dialog.NewsDialog> { key ->
                    val newsViewModel =
                        hiltViewModel<NewsViewModel, NewsViewModel.Factory> { factory ->
                            factory.create(key.newsId)
                        }
                    val newsState = newsViewModel.newsState.collectAsStateWithLifecycle().value

                    NewsDialog(
                        newsState = newsState,
                        onBack = {
                            appUiState.appBackStack.onBack()
                        },
                    )
                }

                entry<Route.Dialog.AddEventDialog> { key ->
                    AddEditEventDialog(
                        namedSchedule = key.namedSchedule,
                        schedule = key.schedule,
                        updatableEvent = key.event,
                        currentDateTime = currentDateTime,
                        appUiState = appUiState,
                        scheduleViewModel = scheduleViewModel
                    )
                }
                entry<Route.Dialog.SearchDialog> {
                    val searchViewModel = hiltViewModel<SearchViewModel>()

                    val searchParams =
                        searchViewModel.searchParams.collectAsStateWithLifecycle().value
                    val searchState =
                        searchViewModel.searchState.collectAsStateWithLifecycle().value

                    SearchDialog(
                        scheduleViewModel = scheduleViewModel,
                        searchViewModel = searchViewModel,
                        appBackStack = appUiState.appBackStack,
                        searchParams = searchParams,
                        searchState = searchState
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
                        scheduleViewModel = scheduleViewModel
                    )
                }
                entry<Route.Dialog.RenameNamedScheduleDialog> { key ->
                    RenameDialog(
                        namedSchedule = key.namedSchedule,
                        appBackStack = appUiState.appBackStack,
                        scheduleViewModel = scheduleViewModel
                    )
                }
                entry<Route.Dialog.HiddenEventsDialog> { key ->
                    HiddenEventsDialog(
                        namedSchedule = key.namedSchedule,
                        schedule = scheduleState.scheduleUiDto?.schedule,
                        hiddenEvents = scheduleState.scheduleUiDto?.hiddenEvents
                            ?: listOf(),
                        scheduleViewModel = scheduleViewModel,
                        appBackStack = appUiState.appBackStack
                    )
                }
            }
        )
    }
}

@Composable
fun PageHost(
    mainViewModel: MainViewModel,
    scheduleViewModel: ScheduleViewModel,
    preferencesViewModel: PreferencesViewModel,

    scheduleState: ScheduleState,
    currentState: CurrentState,
    namedScheduleState: NamedScheduleState,
    appState: AppState,
    currentDateTime: LocalDateTime,

    appUiState: AppUiState,
    scheduleUiState: ScheduleUiState?,
    reviewUiState: ReviewUiState,

    appSettings: AppSettings
) {
    val barItems = arrayOf(
        BarItem(
            title = R.string.review,
            iconRes = R.drawable.review,
            selectedIconRes = R.drawable.review_fill,
            page = Route.Page.Review
        ),
        BarItem(
            title = R.string.schedule,
            iconRes = R.drawable.schedule,
            selectedIconRes = R.drawable.schedule_fill,
            page = Route.Page.Schedule,
            onClick = scheduleUiState?.let {
                {
                    appUiState.scope.launch {
                        when {
                            scheduleState.scheduleUiDto?.schedulePagerUiDto != null && appSettings.scheduleView == ScheduleView.CALENDAR -> {
                                scheduleUiState.onSelectDate(
                                    requireNotNull(scheduleState.scheduleUiDto).schedulePagerUiDto.defaultDate
                                )
                                scheduleUiState.pagerWeeksState.animateScrollToPage(
                                    requireNotNull(scheduleState.scheduleUiDto).schedulePagerUiDto.weeksStartIndex
                                )
                            }

                            appSettings.scheduleView == ScheduleView.LIST -> {
                                scheduleUiState.scheduleListState.animateScrollToItem(0)
                            }
                        }
                    }
                }
            }
        ),
        BarItem(
            title = R.string.news,
            iconRes = R.drawable.news,
            page = Route.Page.NewsList,
            onClick = {
                appUiState.scope.launch {
                    appUiState.newsListState.animateScrollToItem(0)
                }
            },
        ),
        BarItem(
            title = R.string.settings,
            iconRes = R.drawable.settings,
            selectedIconRes = R.drawable.settings_fill,
            page = Route.Page.Settings,
            onClick = {
                appUiState.scope.launch {
                    appUiState.settingsListState.animateScrollToItem(0)
                }
            }
        ),
    )

    val navigate: (Route.Page) -> Unit = remember {
        { page ->
            appUiState.appBackStack.openPage(page)
        }
    }

    val selectedPage = appUiState.appBackStack.lastPage()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            if (appUiState.appBackStack.dialogBackStack.size <= 1) {
                CustomSnackbarHost(
                    snackBarHostState = appUiState.snackBarHostState
                )
            }
        },
        bottomBar = {
            CustomNavigationBar(
                currentPageIndex = appUiState.appBackStack.lastPage().index,
                barItems = {
                    CustomNavigationBarItem(
                        barItem = barItems[0],
                        selectedPage = selectedPage,
                        navigate = navigate
                    )
                    CustomNavigationBarItem(
                        barItem = barItems[1],
                        selectedPage = selectedPage,
                        navigate = navigate
                    )
                    CustomNavigationBarItem(
                        barItem = barItems[2],
                        selectedPage = selectedPage,
                        navigate = navigate
                    )
                    CustomNavigationBarItem(
                        barItem = barItems[3],
                        selectedPage = selectedPage,
                        showBadge = appState.updatesAvailable,
                        navigate = navigate
                    )
                },
                isDarkTheme = appSettings.decorPreferences.theme.isDarkTheme()
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
                        currentState = currentState,
                        reviewUiState = reviewUiState,
                        currentDateTime = currentDateTime,
                        scheduleViewModel = scheduleViewModel,
                        appBackStack = appUiState.appBackStack,
                        isDarkTheme = appSettings.decorPreferences.theme.isDarkTheme(),
                        externalPadding = padding
                    )
                }

                entry<Route.Page.Schedule> {
                    ScreenSchedule(
                        appUiState = appUiState,

                        currentState = currentState,
                        namedScheduleState = namedScheduleState,
                        scheduleState = scheduleState,

                        scheduleUiState = scheduleUiState,
                        appSettings = appSettings,
                        currentDateTime = currentDateTime,
                        scheduleViewModel = scheduleViewModel,
                        preferencesViewModel = preferencesViewModel,
                        externalPadding = padding
                    )
                }

                entry<Route.Page.NewsList> {
                    val newsListViewModel = hiltViewModel<NewsListViewModel>()

                    NewsScreen(
                        newsListFlow = newsListViewModel.newsListFlow,
                        onGetNewsById = { id ->
                            appUiState.appBackStack.openDialog(
                                Route.Dialog.NewsDialog(id)
                            )
                        },
                        newsGridListState = appUiState.newsListState,
                        externalPadding = padding
                    )
                }

                entry<Route.Page.Settings> {
                    SettingsScreen(
                        appUiState = appUiState,
                        appSettings = appSettings,
                        appState = appState,
                        preferencesViewModel = preferencesViewModel,
                        mainViewModel = mainViewModel,
                        externalPadding = padding
                    )
                }
            }
        )
    }
}