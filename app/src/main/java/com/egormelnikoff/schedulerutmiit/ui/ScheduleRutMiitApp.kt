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
import com.egormelnikoff.schedulerutmiit.app.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.dialogs.AddScheduleDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.CurriculumDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.EventDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.HiddenEventsDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.NewsDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.RenameDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.SearchDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event.AddEditEventDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomNavigationBar
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSnackbarHost
import com.egormelnikoff.schedulerutmiit.ui.elements.barItems
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.ui.screens.news.NewsScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleUiEventProcessor
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleUiStateSynchronizer
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ReviewUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.view_models.curriculum.CurriculumViewModel
import com.egormelnikoff.schedulerutmiit.view_models.news.state.NewsState
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsViewModel
import com.egormelnikoff.schedulerutmiit.view_models.schedule.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.view_models.search.state.SearchParams
import com.egormelnikoff.schedulerutmiit.view_models.search.state.SearchState
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModel
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun ScheduleRutMiitApp(
    scheduleViewModel: ScheduleViewModel,
    searchViewModel: SearchViewModel,
    newsViewModel: NewsViewModel,
    settingsViewModel: SettingsViewModel,
    appSettings: AppSettings
) {
    val searchParams = searchViewModel.searchParams.collectAsStateWithLifecycle().value
    val searchState = searchViewModel.searchState.collectAsStateWithLifecycle().value
    val scheduleState = scheduleViewModel.scheduleState.collectAsStateWithLifecycle().value
    val newsState = newsViewModel.newsState.collectAsStateWithLifecycle().value
    val currentDateTime = settingsViewModel.currentDate.collectAsStateWithLifecycle().value

    val appUiState = AppUiState()
    val scheduleUiState = ScheduleUiState(scheduleState)
    val reviewUiState = ReviewUiState()

    ScheduleUiEventProcessor(
        scheduleViewModel = scheduleViewModel,
        snackBarHostState = appUiState.snackBarHostState
    )

    ScheduleUiStateSynchronizer(
        scheduleUiState = scheduleUiState,
        scheduleState = scheduleState,
        scheduleViewModel = scheduleViewModel,
        currentDateTime = currentDateTime
    )

    Box(Modifier.fillMaxSize()) {
        RootHost(
            pageHost = {
                PageHost(
                    appUiState = appUiState,
                    scheduleViewModel = scheduleViewModel,
                    currentDateTime = currentDateTime,
                    newsViewModel = newsViewModel,
                    settingsViewModel = settingsViewModel,
                    scheduleState = scheduleState,
                    scheduleUiState = scheduleUiState,
                    reviewUiState = reviewUiState,
                    appSettings = appSettings
                )
            },
            scheduleViewModel = scheduleViewModel,
            newsViewModel = newsViewModel,
            searchViewModel = searchViewModel,

            appUiState = appUiState,
            scheduleState = scheduleState,
            newsState = newsState,
            searchState = searchState,
            searchParams = searchParams,
            currentDateTime = currentDateTime
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RootHost(
    pageHost: @Composable () -> Unit,
    scheduleViewModel: ScheduleViewModel,
    searchViewModel: SearchViewModel,
    newsViewModel: NewsViewModel,

    appUiState: AppUiState,
    searchState: SearchState,
    searchParams: SearchParams,
    scheduleState: ScheduleState,
    newsState: NewsState,
    currentDateTime: LocalDateTime,
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
                        namedScheduleEntity = key.namedScheduleEntity,
                        scheduleEntity = key.scheduleEntity,
                        isSavedSchedule = key.isSavedSchedule,
                        event = key.event,
                        eventExtraData = key.eventExtraData,
                        appBackStack = appUiState.appBackStack,
                        scheduleViewModel = scheduleViewModel,
                        searchViewModel = searchViewModel
                    )
                }

                entry<Route.Dialog.NewsDialog> {
                    NewsDialog(
                        setDefaultState = {
                            newsViewModel.setDefaultNewsState()
                        },
                        newsState = newsState,
                        appBackStack = appUiState.appBackStack,
                    )
                }

                entry<Route.Dialog.AddEventDialog> { key ->
                    AddEditEventDialog(
                        namedScheduleEntity = key.namedScheduleEntity,
                        scheduleEntity = key.scheduleEntity,
                        editableEvent = key.event,
                        currentDateTime = currentDateTime,
                        appUiState = appUiState,
                        scheduleViewModel = scheduleViewModel
                    )
                }
                entry<Route.Dialog.SearchDialog> {
                    SearchDialog(
                        scheduleViewModel = scheduleViewModel,
                        searchViewModel = searchViewModel,
                        appBackStack = appUiState.appBackStack,
                        searchParams = searchParams,
                        searchState = searchState,

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
                        namedScheduleEntity = key.namedScheduleEntity,
                        appBackStack = appUiState.appBackStack,
                        scheduleViewModel = scheduleViewModel
                    )
                }
                entry<Route.Dialog.HiddenEventsDialog> { key ->
                    HiddenEventsDialog(
                        namedScheduleEntity = key.namedScheduleEntity,
                        scheduleEntity = scheduleState.currentNamedSchedule?.scheduleUiDto?.scheduleEntity,
                        hiddenEvents = scheduleState.currentNamedSchedule?.scheduleUiDto?.hiddenEvents ?: listOf(),
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
    scheduleViewModel: ScheduleViewModel,
    newsViewModel: NewsViewModel,
    settingsViewModel: SettingsViewModel,

    scheduleState: ScheduleState,
    currentDateTime: LocalDateTime,

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
                                scheduleUiState != null && scheduleState.currentNamedSchedule?.scheduleUiDto?.schedulePagerUiDto != null && appSettings.scheduleView == ScheduleView.CALENDAR -> {
                                    scheduleUiState.onSelectDate(
                                        scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.defaultDate
                                    )
                                    scheduleUiState.pagerWeeksState.animateScrollToPage(
                                        scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.weeksStartIndex
                                    )
                                }

                                scheduleUiState != null && appSettings.scheduleView == ScheduleView.LIST -> {
                                    scheduleUiState.scheduleListState.animateScrollToItem(0)
                                }

                                scheduleUiState != null && scheduleState.currentNamedSchedule?.scheduleUiDto?.schedulePagerUiDto != null && appSettings.scheduleView == ScheduleView.SPLIT_WEEKS -> {
                                    scheduleUiState.pagerSplitWeeks.scrollToPage(
                                        scheduleState.currentNamedSchedule.scheduleUiDto.schedulePagerUiDto.defaultDate.dayOfWeek.value - 1
                                    )
                                    scheduleUiState.onSelectWeek(
                                        scheduleState.currentNamedSchedule.scheduleUiDto.scheduleEntity.recurrence?.currentNumber
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
                        scheduleState = scheduleState,
                        scheduleUiState = scheduleUiState,
                        appSettings = appSettings,
                        currentDateTime = currentDateTime,
                        scheduleViewModel = scheduleViewModel,
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
                        appBackStack = appUiState.appBackStack,
                        externalPadding = padding
                    )
                }

                entry<Route.Page.Settings> {
                    SettingsScreen(
                        appUiState = appUiState,
                        appSettings = appSettings,
                        settingsViewModel = settingsViewModel,
                        externalPadding = padding
                    )
                }
            }
        )
    }
}