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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.app.model.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.datastore.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.ui.dialogs.AddEventDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.AddScheduleDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.EventDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.HiddenEventsDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.InfoDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.NewsDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.RenameDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.SearchDialog
import com.egormelnikoff.schedulerutmiit.ui.dialogs.SearchOption
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomNavigationBar
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSnackbarHost
import com.egormelnikoff.schedulerutmiit.ui.elements.rememberBarItems
import com.egormelnikoff.schedulerutmiit.ui.navigation.Routes
import com.egormelnikoff.schedulerutmiit.ui.screens.news.NewsScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.state.ReviewState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.ui.state.rememberAppState
import com.egormelnikoff.schedulerutmiit.ui.state.rememberReviewState
import com.egormelnikoff.schedulerutmiit.ui.state.rememberScheduleState
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsViewModel
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.view_models.schedule.UiEvent
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModel
import com.egormelnikoff.schedulerutmiit.view_models.settings.AppInfoState
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit

@Composable
fun Main(
    searchViewModel: SearchViewModel,
    scheduleViewModel: ScheduleViewModel,
    newsViewModel: NewsViewModel,
    settingsViewModel: SettingsViewModel,
    preferencesDataStore: PreferencesDataStore,
    logger: Logger,
    appSettings: AppSettings,
) {
    val searchUiState = searchViewModel.uiState.collectAsState().value
    val scheduleUiState = scheduleViewModel.uiState.collectAsState().value
    val newsUiState = newsViewModel.uiState.collectAsState().value
    val appInfoState = settingsViewModel.stateAppInfo.collectAsState().value

    val appState = rememberAppState()
    val scheduleState = rememberScheduleState(scheduleUiState)
    val reviewState = rememberReviewState()

    UiEventProcessor(
        scheduleViewModel = scheduleViewModel,
        snackBarHostState = appState.snackBarHostState
    )
    ScheduleStateSynchronizer(
        scheduleUiState = scheduleUiState,
        scheduleState = scheduleState,
        reviewState = reviewState
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomNavigationBar(
                appBackStack = appState.appBackStack,
                barItems = rememberBarItems(
                    onScheduleClick = {
                        appState.scope.launch {
                            when {
                                scheduleState != null && appSettings.calendarView -> {
                                    scheduleState.onSelectDate(
                                        scheduleUiState.currentNamedScheduleData!!.schedulePagerData.defaultDate
                                    )
                                    scheduleState.pagerWeeksState.animateScrollToPage(
                                        scheduleUiState.currentNamedScheduleData.schedulePagerData.weeksStartIndex
                                    )
                                }

                                scheduleState != null && !appSettings.calendarView -> {
                                    scheduleState.scheduleListState.animateScrollToItem(0)
                                }
                            }
                        }
                    },
                    onNewsClick = {
                        appState.scope.launch {
                            appState.newsListState.animateScrollToItem(0)
                        }
                    },
                    onSettingsClick = {
                        appState.scope.launch {
                            appState.settingsListState.animateScrollToItem(0)
                        }
                    }
                ),
                theme = appSettings.theme
            )
        },
        snackbarHost = {
            CustomSnackbarHost(
                snackBarHostState = appState.snackBarHostState
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
            backStack = appState.appBackStack.backStack,
            onBack = {
                appState.appBackStack.onBack()
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
                            appState.appBackStack.navigateToDialog(Routes.SearchDialog)
                        },
                        navigateToAddSchedule = {
                            appState.appBackStack.navigateToDialog(Routes.AddScheduleDialog)
                        },
                        navigateToEvent = { value ->
                            appState.appBackStack.navigateToDialog(
                                Routes.EventDialog(
                                    event = value.first,
                                    eventExtraData = value.second
                                )
                            )
                        },
                        navigateToRenameDialog = { namedScheduleEntity ->
                            appState.appBackStack.navigateToDialog(
                                Routes.RenameNamedScheduleDialog(
                                    namedScheduleEntity = namedScheduleEntity
                                )
                            )
                        },
                        onSetNamedSchedule = { value ->
                            if (value != scheduleUiState.currentNamedScheduleData?.namedSchedule?.namedScheduleEntity?.id) {
                                scheduleViewModel.getNamedScheduleFromDb(
                                    primaryKeyNamedSchedule = value
                                )
                            }
                            appState.appBackStack.navigateToPage(Routes.Schedule)
                        },
                        onSelectDefaultNamedSchedule = { value ->
                            scheduleViewModel.getNamedScheduleFromDb(
                                primaryKeyNamedSchedule = value,
                                setDefault = true
                            )
                        },
                        onDeleteNamedSchedule = { value ->
                            scheduleViewModel.deleteNamedSchedule(
                                primaryKeyNamedSchedule = value.first,
                                isDefault = value.second
                            )
                        },

                        scheduleUiState = scheduleUiState,
                        reviewState = reviewState
                    )
                }

                entry<Routes.Schedule> {
                    ScreenSchedule(
                        externalPadding = externalPadding,
                        navigateToSearch = {
                            appState.appBackStack.navigateToDialog(Routes.SearchDialog)
                        },
                        navigateToAddSchedule = {
                            appState.appBackStack.navigateToDialog(Routes.AddScheduleDialog)
                        },
                        navigateToAddEvent = { value ->
                            appState.appBackStack.navigateToDialog(
                                Routes.AddEventDialog(
                                    scheduleEntity = value
                                )
                            )
                        },
                        navigateToEvent = { value ->
                            appState.appBackStack.navigateToDialog(
                                Routes.EventDialog(
                                    event = value.first,
                                    eventExtraData = value.second
                                )
                            )
                        },
                        navigateToRenameDialog = { namedScheduleEntity ->
                            appState.appBackStack.navigateToDialog(
                                Routes.RenameNamedScheduleDialog(
                                    namedScheduleEntity = namedScheduleEntity
                                )
                            )
                        },
                        navigateToHiddenEvents = {
                            appState.appBackStack.navigateToDialog(
                                Routes.HiddenEventsDialog
                            )
                        },

                        onLoadInitialData = {
                            scheduleViewModel.refreshScheduleState(false)
                        },
                        onRefreshState = { primaryKey ->
                            scheduleViewModel.refreshScheduleState(
                                showLoading = false,
                                showUpdating = true,
                                namedSchedulePrimaryKey = primaryKey
                            )
                        },
                        onSaveCurrentNamedSchedule = {
                            scheduleViewModel.saveCurrentNamedSchedule()
                        },
                        onSelectDefaultNamedSchedule = { value ->
                            scheduleViewModel.getNamedScheduleFromDb(
                                primaryKeyNamedSchedule = value,
                                setDefault = true
                            )
                        },
                        onDeleteNamedSchedule = { value ->
                            scheduleViewModel.deleteNamedSchedule(
                                primaryKeyNamedSchedule = value.first,
                                isDefault = value.second
                            )
                        },
                        onDeleteEvent = { primaryKey ->
                            scheduleViewModel.deleteCustomEvent(
                                scheduleEntity = scheduleUiState.currentNamedScheduleData!!.settledScheduleEntity!!,
                                primaryKeyEvent = primaryKey
                            )
                        },
                        onHideEvent = { primaryKey ->
                            scheduleViewModel.updateEventHidden(
                                scheduleEntity = scheduleUiState.currentNamedScheduleData!!.settledScheduleEntity!!,
                                eventPrimaryKey = primaryKey,
                                isHidden = true
                            )
                        },
                        onSetDefaultSchedule = { value ->
                            scheduleViewModel.setDefaultSchedule(
                                primaryKeyNamedSchedule = value.first,
                                primaryKeySchedule = value.second,
                                timetableId = value.third
                            )
                        },

                        onSetScheduleView = { newValue ->
                            appState.scope.launch {
                                preferencesDataStore.setScheduleView(newValue)
                            }
                        },

                        appSettings = appSettings,
                        scheduleState = scheduleState,
                        scheduleUiState = scheduleUiState,
                    )
                }

                entry<Routes.NewsList> {
                    NewsScreen(
                        externalPadding = externalPadding,
                        onGetNewsById = { value ->
                            newsViewModel.getNewsById(value)
                        },
                        onShowDialogNews = {
                            appState.appBackStack.navigateToDialog(Routes.NewsDialog)
                        },
                        newsUiState = newsUiState,
                        newsListFLow = newsViewModel.newsListFlow,
                        newsGridListState = appState.newsListState
                    )
                }

                entry<Routes.Settings> {
                    SettingsScreen(
                        externalPadding = externalPadding,
                        onShowDialogInfo = {
                            appState.appBackStack.navigateToDialog(Routes.InfoDialog)
                        },
                        onSendLogs = {
                            logger.sendLogFile(appState.context)
                        },
                        onOpenUri = { value ->
                            appState.uriHandler.openUri(value)
                        },
                        onSetViewEvent = { value ->
                            appState.scope.launch {
                                preferencesDataStore.setViewEvent(value)
                            }
                        },
                        onSetShowCountClasses = { value ->
                            appState.scope.launch {
                                preferencesDataStore.setShowCountClasses(value)
                            }
                        },
                        onSetTheme = { value ->
                            appState.scope.launch {
                                preferencesDataStore.setTheme(value)
                            }
                        },
                        onSetDecorColor = { value ->
                            appState.scope.launch {
                                preferencesDataStore.setDecorColor(value)
                            }
                        },
                        appSettings = appSettings,
                        settingsListState = appState.settingsListState
                    )
                }

                entry<Routes.EventDialog> { key ->
                    EventDialog(
                        externalPadding = externalPadding,
                        onBack = { appState.appBackStack.onBack() },
                        navigateToSchedule = {
                            appState.appBackStack.navigateToPage(Routes.Schedule)
                        },
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
                        onDeleteEvent = { primaryKey ->
                            scheduleViewModel.deleteCustomEvent(
                                scheduleEntity = scheduleUiState.currentNamedScheduleData.settledScheduleEntity!!,
                                primaryKeyEvent = primaryKey
                            )
                        },
                        onHideEvent = { primaryKey ->
                            scheduleViewModel.updateEventHidden(
                                scheduleEntity = scheduleUiState.currentNamedScheduleData.settledScheduleEntity!!,
                                eventPrimaryKey = primaryKey,
                                isHidden = true
                            )
                        },
                        onShowEvent = { primaryKey ->
                            scheduleViewModel.updateEventHidden(
                                scheduleEntity = scheduleUiState.currentNamedScheduleData.settledScheduleEntity!!,
                                eventPrimaryKey = primaryKey,
                                isHidden = false
                            )
                        },
                        event = key.event,
                        eventExtraData = key.eventExtraData,
                        isSavedSchedule = scheduleUiState.isSaved,
                        isCustomSchedule = scheduleUiState.currentNamedScheduleData!!.namedSchedule!!.namedScheduleEntity.type == 3
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
                        onBack = { appState.appBackStack.onBack() },
                        onLoadAppInfoState = {
                            if (appInfoState !is AppInfoState.Loaded) {
                                settingsViewModel.getAppInfo()
                            }
                        },
                        onOpenUri = { value ->
                            appState.uriHandler.openUri(value)
                        },
                        appInfoState = appInfoState
                    )
                }

                entry<Routes.AddEventDialog> { key ->
                    AddEventDialog(
                        externalPadding = externalPadding,
                        onBack = {
                            appState.appBackStack.onBack()
                        },
                        onAddCustomEvent = { event ->
                            scheduleViewModel.addCustomEvent(
                                scheduleEntity = key.scheduleEntity,
                                event = event
                            )
                        },
                        onShowErrorMessage = { message ->
                            appState.scope.launch {
                                appState.snackBarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Long
                                )
                            }
                        },
                        scheduleEntity = key.scheduleEntity,
                        focusManager = appState.focusManager
                    )
                }

                entry<Routes.SearchDialog> {
                    SearchDialog(
                        externalPadding = externalPadding,
                        onSetDefaultState = {
                            searchViewModel.setDefaultSearchState()
                            reviewState.onChangeQuery("")
                            reviewState.onSelectSearchOption(SearchOption.ALL)
                        },
                        onSearchSchedule = { value ->
                            scheduleViewModel.getNamedScheduleFromApi(
                                name = value.first,
                                apiId = value.second,
                                type = value.third
                            )
                            appState.appBackStack.navigateToPage(Routes.Schedule)
                            searchViewModel.setDefaultSearchState()
                            reviewState.onChangeQuery("")
                            reviewState.onSelectSearchOption(SearchOption.ALL)
                        },
                        onSearch = { value ->
                            searchViewModel.search(
                                query = value.first,
                                selectedSearchOption = value.second
                            )
                        },
                        searchUiState = searchUiState,
                        reviewState = reviewState
                    )
                }

                entry<Routes.AddScheduleDialog> {
                    AddScheduleDialog(
                        externalPadding = externalPadding,
                        onBack = {
                            appState.appBackStack.onBack()
                        },
                        onAddCustomSchedule = { value ->
                            scheduleViewModel.addCustomSchedule(
                                name = value.first,
                                startDate = value.second,
                                endDate = value.third,
                            )
                            appState.appBackStack.navigateToPage(Routes.Schedule)
                        },
                        onShowErrorMessage = { message ->
                            appState.scope.launch {
                                appState.snackBarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Long
                                )
                            }
                        },
                        focusManager = appState.focusManager
                    )
                }

                entry<Routes.RenameNamedScheduleDialog> { key ->
                    RenameDialog(
                        oldName = key.namedScheduleEntity.fullName,
                        onBack = {
                            appState.appBackStack.onBack()
                        },
                        onConfirm = { newName ->
                            scheduleViewModel.renameNamedSchedule(
                                namedScheduleEntity = key.namedScheduleEntity,
                                newName = newName
                            )
                            appState.appBackStack.onBack()
                        },
                        externalPadding = externalPadding
                    )
                }

                entry<Routes.HiddenEventsDialog> {
                    HiddenEventsDialog(
                        onBack = {
                            appState.appBackStack.onBack()
                        },
                        onShowEvent = { primaryKey ->
                            scheduleViewModel.updateEventHidden(
                                scheduleEntity = scheduleUiState.currentNamedScheduleData.settledScheduleEntity!!,
                                eventPrimaryKey = primaryKey,
                                isHidden = false
                            )
                        },
                        hiddenEvents = scheduleUiState.currentNamedScheduleData!!.hiddenEvents,
                        externalPadding = externalPadding
                    )
                }
            }
        )
    }
}

@Composable
fun UiEventProcessor(
    scheduleViewModel: ScheduleViewModel,
    snackBarHostState: SnackbarHostState
) {
    LaunchedEffect(Unit) {
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
}

@Composable
fun ScheduleStateSynchronizer(
    scheduleUiState: ScheduleUiState,
    scheduleState: ScheduleState?,
    reviewState: ReviewState
) {
    if (scheduleUiState.currentNamedScheduleData?.settledScheduleEntity != null && scheduleState != null) {
        LaunchedEffect(
            scheduleUiState.currentNamedScheduleData.namedSchedule!!.namedScheduleEntity.apiId,
            scheduleUiState.currentNamedScheduleData.settledScheduleEntity.timetableId
        ) {
            scheduleState.onExpandSchedulesMenu(false)
            reviewState.onChangeVisibilityHiddenEvents(false)
            scheduleState.pagerDaysState.scrollToPage(
                scheduleUiState.currentNamedScheduleData.schedulePagerData.daysStartIndex
            )
            scheduleState.scheduleListState.scrollToItem(0)
        }
        LaunchedEffect(scheduleState.selectedDate) {
            val targetPage = ChronoUnit.DAYS.between(
                scheduleUiState.currentNamedScheduleData.settledScheduleEntity.startDate,
                scheduleState.selectedDate
            ).toInt()

            if (scheduleState.pagerDaysState.currentPage != targetPage) {
                scheduleState.pagerDaysState.scrollToPage(targetPage)
            }
        }

        LaunchedEffect(scheduleState.pagerDaysState.currentPage) {
            val newSelectedDate =
                scheduleUiState.currentNamedScheduleData.settledScheduleEntity.startDate.plusDays(
                    scheduleState.pagerDaysState.currentPage.toLong()
                )
            scheduleState.onSelectDate(newSelectedDate)

            val targetWeekIndex = ChronoUnit.WEEKS.between(
                scheduleUiState.currentNamedScheduleData.settledScheduleEntity.startDate
                    .getFirstDayOfWeek(),
                newSelectedDate.getFirstDayOfWeek()
            ).toInt()

            if (scheduleState.pagerWeeksState.currentPage != targetWeekIndex) {
                scheduleState.pagerWeeksState.animateScrollToPage(targetWeekIndex)
            }
        }
    }
}