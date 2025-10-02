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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.AppSettings
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.ui.dialogs.AddEventDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.AddScheduleDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.EventDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.InfoDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.NewsDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.Options
import com.egormelnikoff.schedulerutmiit.ui.dialogs.SearchScheduleDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.BarItem
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomNavigationBar
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSnackbarHost
import com.egormelnikoff.schedulerutmiit.ui.screens.news.NewsScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleCalendarState
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calculateFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.news.NewsViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.UiEvent
import com.egormelnikoff.schedulerutmiit.ui.view_models.search.SearchViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.settings.SettingsViewModel
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
    val today by remember { mutableStateOf(LocalDate.now()) }
    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }
    val scheduleListState = rememberLazyListState()
    val newsListState = rememberLazyStaggeredGridState()
    val settingsListState = rememberLazyStaggeredGridState()

    val searchUiState = searchViewModel.uiState.collectAsState().value
    val scheduleUiState = scheduleViewModel.uiState.collectAsState().value
    val newsUiState = newsViewModel.uiState.collectAsState().value
    val appInfoState = settingsViewModel.stateAppInfo.collectAsState().value

    var visibleSavedSchedules by remember { mutableStateOf(true) }
    var visibleHiddenEvents by remember { mutableStateOf(false) }

    var selectedOption by remember { mutableStateOf(Options.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    var expandedSchedulesMenu by remember { mutableStateOf(false) }
    var selectedDate by remember(
        scheduleUiState.currentScheduleData?.namedSchedule?.namedScheduleEntity?.apiId
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
        scheduleUiState.currentScheduleData?.namedSchedule?.namedScheduleEntity?.apiId,
        scheduleUiState.currentScheduleData?.settledScheduleEntity?.timetableId
    ) {
        expandedSchedulesMenu = false
        pagerDaysState.scrollToPage(scheduleUiState.currentScheduleData?.daysStartIndex ?: 0)
        scheduleListState.scrollToItem(0)
    }

    if (scheduleUiState.currentScheduleData?.settledScheduleEntity != null) {
        LaunchedEffect(selectedDate) {
            val targetPage = ChronoUnit.DAYS.between(
                scheduleUiState.currentScheduleData.settledScheduleEntity.startDate,
                selectedDate
            ).toInt()

            if (pagerDaysState.currentPage != targetPage) {
                pagerDaysState.scrollToPage(targetPage)
            }
        }

        LaunchedEffect(pagerDaysState.currentPage) {
            val newSelectedDate =
                scheduleUiState.currentScheduleData.settledScheduleEntity.startDate.plusDays(
                    pagerDaysState.currentPage.toLong()
                )
            selectedDate = newSelectedDate

            val targetWeekIndex = ChronoUnit.WEEKS.between(
                calculateFirstDayOfWeek(scheduleUiState.currentScheduleData.settledScheduleEntity.startDate),
                calculateFirstDayOfWeek(newSelectedDate)
            ).toInt()

            if (pagerWeeksState.currentPage != targetWeekIndex) {
                pagerWeeksState.animateScrollToPage(targetWeekIndex)
            }
        }
    }

    val barItems = arrayOf(
        BarItem(
            title = LocalContext.current.getString(R.string.review),
            icon = ImageVector.vectorResource(R.drawable.review),
            selectedIcon = ImageVector.vectorResource(R.drawable.review),
            route = Routes.Review,
            onClick = {
                if (appBackStack.last() is Routes.SearchDialog || appBackStack.last() is Routes.AddScheduleDialog) {
                    appBackStack.onBack()
                }
            }
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.schedule),
            icon = ImageVector.vectorResource(R.drawable.schedule),
            selectedIcon = ImageVector.vectorResource(R.drawable.schedule_fill),
            route = Routes.Schedule,
            onClick = {
                if (appBackStack.last() is Routes.EventDialog || appBackStack.last() is Routes.AddEventDialog) {
                    appBackStack.onBack()
                } else {
                    scope.launch {
                        if (appSettings.calendarView) {
                            selectedDate = scheduleUiState.currentScheduleData?.defaultDate ?: today
                            pagerWeeksState.animateScrollToPage(scheduleUiState.currentScheduleData?.weeksStartIndex ?: 0)
                        } else {
                            scheduleListState.animateScrollToItem(0)
                        }
                    }
                }
            }
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.news),
            icon = ImageVector.vectorResource(R.drawable.news),
            selectedIcon = ImageVector.vectorResource(R.drawable.news_fill),
            route = Routes.NewsList,
            onClick = {
                if (appBackStack.last() is Routes.NewsDialog) {
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
                if (appBackStack.last() is Routes.InfoDialog) {
                    appBackStack.onBack()
                } else {
                    scope.launch {
                        settingsListState.animateScrollToItem(0)
                    }
                }
            }
        )
    )

    val onSelectDefaultNamedSchedule: (Long) -> Unit = { value ->
        scheduleViewModel.getNamedScheduleFromDb(
            primaryKeyNamedSchedule = value,
            setDefault = true
        )
    }
    val onSetNamedSchedule: (Long) -> Unit = { value ->
        if (value != scheduleUiState.currentScheduleData?.namedSchedule?.namedScheduleEntity?.id) {
            scheduleViewModel.getNamedScheduleFromDb(
                primaryKeyNamedSchedule = value
            )
        }
        appBackStack.navigateToPage(Routes.Schedule)
    }
    val onDeleteNamedSchedule: ( Pair<Long, Boolean>) -> Unit = { value ->
        scheduleViewModel.deleteNamedSchedule(
            primaryKeyNamedSchedule = value.first,
            isDefault = value.second
        )
    }
    val onSetDefaultSchedule: ( Triple<Long, Long, String>) -> Unit = { value ->
        scheduleViewModel.setDefaultSchedule(
            primaryKeyNamedSchedule = value.first,
            primaryKeySchedule = value.second,
            timetableId = value.third
        )
    }

    val onDeleteEvent: (Long) -> Unit = { primaryKey ->
        scheduleViewModel.deleteCustomEvent(
            scheduleEntity = scheduleUiState.currentScheduleData!!.settledScheduleEntity!!,
            primaryKeyEvent = primaryKey
        )
    }
    val onHideEvent: (Long) -> Unit = { primaryKey ->
        scheduleViewModel.updateEventHidden(
            scheduleEntity = scheduleUiState.currentScheduleData!!.settledScheduleEntity!!,
            eventPrimaryKey = primaryKey,
            isHidden = true
        )
    }
    val onShowEvent: (Long) -> Unit = { primaryKey ->
        scheduleViewModel.updateEventHidden(
            scheduleEntity = scheduleUiState.currentScheduleData!!.settledScheduleEntity!!,
            eventPrimaryKey = primaryKey,
            isHidden = false
        )
    }

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
    ) { externalPadding ->
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
                entry<Routes.Review> {
                    ReviewScreen(
                        externalPadding = externalPadding,
                        navigateToSearch = {
                            appBackStack.navigateToDialog(Routes.SearchDialog)
                        },
                        navigateToAddSchedule = {
                            appBackStack.navigateToDialog(Routes.AddScheduleDialog)
                        },
                        navigateToEvent = { value ->
                            appBackStack.navigateToDialog(
                                Routes.EventDialog(
                                    event = value.first,
                                    eventExtraData = value.second
                                )
                            )
                        },
                        onChangeSavedSchedulesVisibility = { newValue ->
                            visibleSavedSchedules = newValue
                        },
                        onChangeHiddenEventsVisibility = { newValue ->
                            visibleHiddenEvents = newValue
                        },

                        onSetNamedSchedule = onSetNamedSchedule,
                        onSelectDefaultNamedSchedule = onSelectDefaultNamedSchedule,
                        onDeleteNamedSchedule = onDeleteNamedSchedule,
                        onShowEvent = onShowEvent,

                        today = today,
                        visibleSavedSchedules = visibleSavedSchedules,
                        visibleHiddenEvents = visibleHiddenEvents,
                        scheduleUiState = scheduleUiState
                    )
                }

                entry<Routes.Schedule> {
                    ScreenSchedule(
                        externalPadding = externalPadding,
                        today = today,
                        navigateToReview = {
                            appBackStack.navigateToPage(Routes.Review)
                        },
                        navigateToAddEvent = { value ->
                            appBackStack.navigateToDialog(
                                Routes.AddEventDialog(
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
                        onShowExpandedMenu = { newValue ->
                            expandedSchedulesMenu = newValue
                        },

                        onLoadInitialData = {
                            scheduleViewModel.loadInitialData(false)
                        },
                        onSaveCurrentNamedSchedule = {
                            scheduleViewModel.saveCurrentNamedSchedule()
                        },
                        onSelectDefaultNamedSchedule = onSelectDefaultNamedSchedule,
                        onDeleteNamedSchedule = onDeleteNamedSchedule,
                        onDeleteEvent = onDeleteEvent,
                        onHideEvent = onHideEvent,
                        onSetDefaultSchedule = onSetDefaultSchedule,

                        onSetScheduleView = { newValue ->
                            scope.launch {
                                preferencesDataStore.setScheduleView(newValue)
                            }
                        },

                        appSettings = appSettings,
                        expandedSchedulesMenu = expandedSchedulesMenu,

                        scheduleUiState = scheduleUiState,
                        scheduleListState = scheduleListState,
                        scheduleCalendarState = ScheduleCalendarState(
                            pagerDaysState = pagerDaysState,
                            pagerWeeksState = pagerWeeksState,
                            selectedDate = selectedDate,
                            selectDate = { newValue -> selectedDate = newValue }
                        )
                    )
                }

                entry<Routes.NewsList> {
                    NewsScreen(
                        onGetNewsList = { value ->
                            newsViewModel.getNewsList(value)
                        },
                        onGetNewsById = { value ->
                            newsViewModel.getNewsById(value)
                        },
                        onShowDialogNews = {
                            appBackStack.navigateToDialog(Routes.NewsDialog)
                        },
                        newsUiState = newsUiState,
                        newsGridListState = newsListState,
                        paddingValues = externalPadding
                    )
                }

                entry<Routes.Settings> {
                    SettingsScreen(
                        externalPadding = externalPadding,
                        onShowDialogInfo = {
                            appBackStack.navigateToDialog(Routes.InfoDialog)
                        },
                        onOpenUri = { value ->
                            uriHandler.openUri(value)
                        },

                        preferencesDataStore = preferencesDataStore,
                        appSettings = appSettings,

                        settingsListState = settingsListState,
                        scope = scope
                    )
                }

                entry<Routes.EventDialog> { key ->
                    EventDialog(
                        externalPadding = externalPadding,
                        onBack = { appBackStack.onBack() },
                        onSearchNamedSchedule = { value ->
                            scheduleViewModel.getNamedScheduleFromApi(
                                name = value.first,
                                apiId = value.second,
                                type = value.third
                            )
                        },
                        onEventExtraChange = { value ->
                            scheduleViewModel.updateEventExtra(
                                event = key.event,
                                comment = value.first,
                                tag = value.second
                            )
                        },
                        onDeleteEvent = onDeleteEvent,
                        onHideEvent = onHideEvent,
                        onShowEvent = onShowEvent,
                        event = key.event,
                        eventExtraData = key.eventExtraData,
                        isSavedSchedule = scheduleUiState.isSaved,
                        isCustomSchedule = scheduleUiState.currentScheduleData!!.namedSchedule!!.namedScheduleEntity.type == 3
                    )
                }

                entry<Routes.NewsDialog> {
                    NewsDialog(
                        externalPadding = externalPadding,
                        newsUiState = newsUiState
                    )
                }

                entry<Routes.InfoDialog> {
                    InfoDialog(
                        externalPadding = externalPadding,
                        onBack = { appBackStack.onBack() },
                        onOpenUri = { value ->
                            uriHandler.openUri(value)
                        },
                        appInfoState = appInfoState
                    )
                }

                entry<Routes.AddEventDialog> { key ->
                    AddEventDialog(
                        externalPadding = externalPadding,
                        onBack = {
                            appBackStack.onBack()
                        },
                        onAddCustomEvent = { event ->
                            scheduleViewModel.addCustomEvent(
                                scheduleEntity = key.scheduleEntity,
                                event = event
                            )
                        },
                        onShowErrorMessage = { message ->
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Long
                                )
                            }
                        },
                        scheduleEntity = key.scheduleEntity,
                        focusManager = focusManager
                    )
                }

                entry<Routes.SearchDialog> {
                    SearchScheduleDialog(
                        externalPadding = externalPadding,

                        searchQuery = searchQuery,
                        onChangeQuery = { newValue ->
                            searchQuery = newValue
                        },
                        selectedOption = selectedOption,
                        onSelectOption = { newValue ->
                            selectedOption = newValue
                            focusManager.clearFocus()
                        },

                        searchUiState = searchUiState,
                        onSetDefaultState = {
                            searchViewModel.setDefaultSearchState()
                        },
                        onSearchSchedule = { value ->
                            scheduleViewModel.getNamedScheduleFromApi(
                                name = value.first,
                                apiId = value.second,
                                type = value.third
                            )
                            appBackStack.navigateToPage(Routes.Schedule)
                            searchViewModel.setDefaultSearchState()
                            searchQuery = ""
                            selectedOption = Options.ALL
                        },
                        onSearch = { value ->
                            searchViewModel.search(value.first, value.second)
                        }
                    )
                }

                entry<Routes.AddScheduleDialog> {
                    AddScheduleDialog(
                        externalPadding = externalPadding,
                        onBack = {
                            appBackStack.onBack()
                        },
                        onAddCustomSchedule = { value ->
                            scheduleViewModel.addCustomSchedule(
                                name = value.first,
                                startDate = value.second,
                                endDate = value.third,
                            )
                            appBackStack.navigateToPage(Routes.Schedule)
                        },
                        onShowErrorMessage = { message ->
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Long
                                )
                            }
                        },
                        focusManager = focusManager
                    )
                }
            }
        )
    }
}