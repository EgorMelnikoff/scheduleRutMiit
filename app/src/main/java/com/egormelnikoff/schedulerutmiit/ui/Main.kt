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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.AppSettings
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.dialogs.AddEventDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.AddScheduleDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.EventDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.InfoDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.NewsDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.BarItem
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomNavigationBar
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSnackbarHost
import com.egormelnikoff.schedulerutmiit.ui.screens.news.NewsScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleCalendarParams
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calculateFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.screens.search.Options
import com.egormelnikoff.schedulerutmiit.ui.screens.search.SearchScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.NewsViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.SearchViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.SettingsViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.UiEvent
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun Main(
    searchViewModel: SearchViewModel,
    scheduleViewModel: ScheduleViewModel,
    newsViewModel: NewsViewModel,
    settingsViewModel: SettingsViewModel,
    preferencesDataStore: DataStore,

    appSettings: AppSettings,
) {
    val appBackStack by remember {
        mutableStateOf(
            AppBackStack(
                startRoute = Routes.Schedule
            )
        )
    }
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    val searchUiState = searchViewModel.uiState.collectAsState().value
    val scheduleUiState = scheduleViewModel.uiState.collectAsState().value
    val newsUiState = newsViewModel.uiState.collectAsState().value
    val appInfoState = settingsViewModel.stateAppInfo.collectAsState().value

    val snackBarHostState = remember { SnackbarHostState() }
    val scheduleListState = rememberLazyListState()
    val newsListState = rememberLazyStaggeredGridState()
    val settingsListState = rememberLazyStaggeredGridState()

    var selectedOption by remember { mutableStateOf(Options.ALL) }
    var query by remember { mutableStateOf("") }
    var namedScheduleActionsDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }
    val today by remember { mutableStateOf(LocalDate.now()) }

    var expandedSchedulesMenu by remember { mutableStateOf(false) }
    var selectedDate by remember(
        scheduleUiState.currentNamedSchedule?.namedScheduleEntity?.apiId
    ) {
        mutableStateOf(
            scheduleUiState.currentScheduleData?.defaultDate ?: today
        )
    }
    val pagerDaysState = rememberPagerState(
        pageCount = { scheduleUiState.currentScheduleData?.weeksCount?.times(7) ?: 0 },
        initialPage = scheduleUiState.currentScheduleData?.daysStartIndex ?: 0
    )
    val pagerWeeksState = rememberPagerState(
        pageCount = { scheduleUiState.currentScheduleData?.weeksCount ?: 0 },
        initialPage = scheduleUiState.currentScheduleData?.weeksStartIndex ?: 0
    )


    LaunchedEffect(key1 = Unit) {
        scheduleViewModel.uiEvent.collect { info ->
            when (info) {
                is UiEvent.ShowErrorMessage -> {
                    snackBarHostState.showSnackbar(
                        message = info.message,
                        duration = SnackbarDuration.Long
                    )
                }

                is UiEvent.ShowInfoMessage -> {
                    snackBarHostState.showSnackbar(
                        message = info.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    LaunchedEffect(
        scheduleUiState.currentNamedSchedule?.namedScheduleEntity?.apiId,
        scheduleUiState.currentScheduleEntity?.timetableId
    ) {
        expandedSchedulesMenu = false
        pagerDaysState.scrollToPage(scheduleUiState.currentScheduleData?.daysStartIndex ?: 0)
        scheduleListState.scrollToItem(0)
    }

    if (scheduleUiState.currentScheduleEntity != null) {
        LaunchedEffect(selectedDate) {
            val targetPage = ChronoUnit.DAYS.between(
                scheduleUiState.currentScheduleEntity.startDate,
                selectedDate
            ).toInt()

            if (pagerDaysState.currentPage != targetPage) {
                pagerDaysState.scrollToPage(targetPage)
            }
        }

        LaunchedEffect(pagerDaysState.currentPage) {
            val newSelectedDate =
                scheduleUiState.currentScheduleEntity.startDate.plusDays(
                    pagerDaysState.currentPage.toLong()
                )
            selectedDate = newSelectedDate

            val targetWeekIndex = ChronoUnit.WEEKS.between(
                calculateFirstDayOfWeek(scheduleUiState.currentScheduleEntity.startDate),
                calculateFirstDayOfWeek(newSelectedDate)
            ).toInt()

            if (pagerWeeksState.currentPage != targetWeekIndex) {
                pagerWeeksState.animateScrollToPage(targetWeekIndex)
            }
        }
    }

    val barItems = arrayOf(
        BarItem(
            title = LocalContext.current.getString(R.string.search),
            icon = ImageVector.vectorResource(R.drawable.search),
            selectedIcon = ImageVector.vectorResource(R.drawable.search),
            route = Routes.Search,
            onClick = {
                if (appBackStack.last() is Routes.AddSchedule) {
                    appBackStack.onBack()
                } else {
                    searchViewModel.setDefaultSearchState()
                    selectedOption = Options.ALL
                    query = ""
                }
            }
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.schedule),
            icon = ImageVector.vectorResource(R.drawable.schedule),
            selectedIcon = ImageVector.vectorResource(R.drawable.schedule_fill),
            route = Routes.Schedule,
            onClick = {
                if (appBackStack.last() is Routes.EventDialog || appBackStack.last() is Routes.AddEvent) {
                    appBackStack.onBack()
                } else if (scheduleUiState.currentNamedSchedule != null && !scheduleUiState.currentNamedSchedule.namedScheduleEntity.isDefault) {
                    scheduleViewModel.loadInitialData(false)
                }
            }
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.news),
            icon = ImageVector.vectorResource(R.drawable.news),
            selectedIcon = ImageVector.vectorResource(R.drawable.news_fill),
            route = Routes.NewsList,
            onClick = {
                if (appBackStack.last() is Routes.News) {
                    appBackStack.onBack()
                } else {
                    scope.launch {
                        newsListState.animateScrollToItem(0)
                    }
                }
            }
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.settings),
            icon = ImageVector.vectorResource(R.drawable.settings),
            selectedIcon = ImageVector.vectorResource(R.drawable.settings_fill),
            route = Routes.Settings,
            onClick = {
                if (appBackStack.last() is Routes.Info) {
                    appBackStack.onBack()
                } else {
                    scope.launch {
                        settingsListState.animateScrollToItem(0)
                    }
                }
            }
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomNavigationBar(
                appBackStack = appBackStack,
                barItems = barItems
            )
        },
        snackbarHost = {
            CustomSnackbarHost(
                snackBarHostState = snackBarHostState
            )
        }
    ) { paddingValues ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                ),
            backStack = appBackStack.backStack,
            onBack = {
                appBackStack.onBack()
            },
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            popTransitionSpec = {
                fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
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
                entry<Routes.Schedule> {
                    ScreenSchedule(
                        navigateToSearch = {
                            appBackStack.navigateToPage(Routes.Search)
                        },
                        navigateToAddEvent = { value ->
                            appBackStack.navigateToDialog(
                                Routes.AddEvent(
                                    scheduleEntity = value
                                )
                            )
                        },
                        navigateToEvent = { value ->
                            appBackStack.navigateToDialog(
                                Routes.EventDialog(
                                    event = value.first,
                                    eventExtraData = value.second
                                )
                            )
                        },

                        scheduleViewModel = scheduleViewModel,
                        preferencesDataStore = preferencesDataStore,
                        appSettings = appSettings,
                        scheduleUiState = scheduleUiState,
                        scheduleListState = scheduleListState,
                        today = today,

                        paddingValues = paddingValues,
                        scheduleCalendarParams = ScheduleCalendarParams(
                            pagerDaysState = pagerDaysState,
                            pagerWeeksState = pagerWeeksState,
                            selectedDate = selectedDate,
                            selectDate = { newValue -> selectedDate = newValue }
                        ),
                        expandedSchedulesMenu = expandedSchedulesMenu,
                        onShowExpandedMenu = { newValue ->
                            expandedSchedulesMenu = newValue
                        }
                    )
                }

                entry(Routes.Search) {
                    SearchScreen(
                        navigateToSchedule = {
                            appBackStack.navigateToPage(Routes.Schedule)
                        },
                        navigateToAddSchedule = {
                            appBackStack.navigateToDialog(Routes.AddSchedule)
                        },
                        query = query,
                        onQueryChanged = { newValue -> query = newValue },
                        namedScheduleActionsDialog = namedScheduleActionsDialog,
                        onShowActionsDialog = { newValue ->
                            namedScheduleActionsDialog = newValue
                        },
                        selectedOption = selectedOption,
                        onSelectOption = { option -> selectedOption = option },

                        scheduleViewModel = scheduleViewModel,
                        searchViewModel = searchViewModel,
                        searchUiState = searchUiState,
                        scheduleUiState = scheduleUiState,
                        paddingValues = paddingValues
                    )
                }

                entry<Routes.NewsList> {
                    NewsScreen(
                        newsViewModel = newsViewModel,
                        onShowDialogNews = {
                            appBackStack.navigateToDialog(Routes.News)
                        },
                        newsUiState = newsUiState,
                        newsGridListState = newsListState,
                        paddingValues = paddingValues
                    )
                }

                entry(Routes.Settings) {
                    SettingsScreen(
                        preferencesDataStore = preferencesDataStore,
                        appSettings = appSettings,

                        onShowDialogInfo = {
                            appBackStack.navigateToDialog(Routes.Info)
                        },
                        settingsListState = settingsListState,
                        paddingValues = paddingValues,
                        uriHandler = uriHandler
                    )
                }

                entry<Routes.EventDialog> { key ->
                    EventDialog(
                        onBack = { appBackStack.onBack() },
                        scheduleViewModel = scheduleViewModel,
                        event = key.event,
                        eventExtraData = key.eventExtraData,
                        isSavedSchedule = scheduleUiState.isSaved,
                        isCustomSchedule = scheduleUiState.currentNamedSchedule!!.namedScheduleEntity.type == 3
                    )
                }

                entry(Routes.News) {
                    NewsDialog(
                        newsUiState = newsUiState,
                        paddingValues = paddingValues
                    )
                }


                entry(Routes.Info) {
                    InfoDialog(
                        onBack = { appBackStack.onBack() },
                        appInfoState = appInfoState,
                        paddingValues = paddingValues,
                        uriHandler = uriHandler
                    )
                }

                entry(Routes.AddSchedule) {
                    AddScheduleDialog(
                        scheduleViewModel = scheduleViewModel,
                        scope = scope,
                        snackbarHostState = snackBarHostState,
                        paddingValues = paddingValues,
                        onBack = {
                            appBackStack.onBack()
                        }
                    )
                }

                entry<Routes.AddEvent> { key ->
                    AddEventDialog(
                        scheduleViewModel = scheduleViewModel,
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        scheduleEntity = key.scheduleEntity,
                        paddingValues = paddingValues,
                        onBack = {
                            appBackStack.onBack()
                        }
                    )
                }
            }
        )
    }
}