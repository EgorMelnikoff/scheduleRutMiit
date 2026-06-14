package com.egormelnikoff.schedulerutmiit.ui

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.egormelnikoff.schedulerutmiit.news.ui.NewsDialog
import com.egormelnikoff.schedulerutmiit.news.ui.NewsScreen
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.EventAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.AddEditEventDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.AddScheduleDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.EventDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.HiddenEventsDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.RenameDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.ScheduleUiStateSynchronizer
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.AppUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.ReviewUiState
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.calendar.scheduleCalendarState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.CurrentState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.ScheduleState
import com.egormelnikoff.schedulerutmiit.ui.screen.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.ui.screen.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.screen.schedule.UiEventProcessor
import com.egormelnikoff.schedulerutmiit.ui.dialog.search.SearchDialog
import com.egormelnikoff.schedulerutmiit.ui.screen.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.screen.tasks.TasksScreen
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
    val namedScheduleState =
        scheduleViewModel.namedScheduleState.collectAsStateWithLifecycle().value
    val scheduleState = scheduleViewModel.scheduleState.collectAsStateWithLifecycle().value

    val hourlyDateTime = mainViewModel.hourlyDateTime.collectAsStateWithLifecycle().value
    val appState = mainViewModel.appState.collectAsStateWithLifecycle().value

    val appUiState = AppUiState()
    val scheduleCalendarState = scheduleCalendarState(namedScheduleState, scheduleState)
    val scheduleListState = rememberLazyListState()
    val reviewUiState = ReviewUiState()

    UiEventProcessor(
        scheduleViewModel = scheduleViewModel,
        mainViewModel = mainViewModel,
        snackBarHostState = appUiState.snackBarHostState
    )

    ScheduleUiStateSynchronizer(
        scheduleCalendarState = scheduleCalendarState,
        scheduleListState = scheduleListState,
        scheduleState = scheduleState,
        namedScheduleState = namedScheduleState,
        scheduleViewModel = scheduleViewModel,
        hourlyDateTime = hourlyDateTime
    )

    Box(Modifier.fillMaxSize()) {
        RootHost(
            pageHost = {
                PageHost(
                    appUiState = appUiState,
                    scheduleCalendarState = scheduleCalendarState,
                    scheduleListState = scheduleListState,
                    reviewUiState = reviewUiState,

                    mainViewModel = mainViewModel,
                    scheduleViewModel = scheduleViewModel,
                    preferencesViewModel = preferencesViewModel,

                    hourlyDateTime = hourlyDateTime,
                    scheduleState = scheduleState,
                    currentState = currentState,
                    namedScheduleState = namedScheduleState,
                    appState = appState,

                    appSettings = appSettings
                )
            },
            scheduleViewModel = scheduleViewModel,

            appUiState = appUiState,
            scheduleState = scheduleState,
            hourlyDateTime = hourlyDateTime
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
    hourlyDateTime: LocalDateTime,

    appUiState: AppUiState,
    scheduleCalendarState: CalendarState?,
    scheduleListState: LazyListState,
    reviewUiState: ReviewUiState,

    appSettings: AppSettings
) {
    val navigate: (Route.Page) -> Unit = remember {
        { page ->
            appUiState.appBackStack.openPage(page)
        }
    }

    val selectedPage = appUiState.appBackStack.lastPage()

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            mainViewModel.importData(
                uri = uri,
                onSuccess = {
                    scheduleViewModel.refreshScheduleState()
                    appUiState.appBackStack.navigateToStartRage()
                }
            )
        }
    }

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
                        barItem = remember {
                            BarItem(
                                title = R.string.review,
                                iconRes = R.drawable.review,
                                selectedIconRes = R.drawable.review_fill,
                                page = Route.Page.Review
                            )
                        },
                        selectedPage = selectedPage,
                        navigate = navigate,
                        onClick = null
                    )
                    CustomNavigationBarItem(
                        barItem = remember {
                            BarItem(
                                title = R.string.schedule,
                                iconRes = R.drawable.schedule,
                                selectedIconRes = R.drawable.schedule_fill,
                                page = Route.Page.Schedule
                            )
                        },
                        selectedPage = selectedPage,
                        navigate = navigate,
                        onClick = scheduleCalendarState?.let {
                            {
                                appUiState.scope.launch {
                                    when {
                                        scheduleState.scheduleUiDto?.calendarData != null && appSettings.scheduleView == ScheduleView.CALENDAR -> {
                                            scheduleCalendarState.onSelectDate(
                                                requireNotNull(scheduleState.scheduleUiDto).calendarData.defaultDate
                                            )
                                            scheduleCalendarState.pagerWeeksState.animateScrollToPage(
                                                requireNotNull(scheduleState.scheduleUiDto).calendarData.weeksPagerDefaultIndex
                                            )
                                        }

                                        appSettings.scheduleView == ScheduleView.LIST -> {
                                            scheduleListState.animateScrollToItem(
                                                0
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                    CustomNavigationBarItem(
                        barItem = remember {
                            BarItem(
                                title = R.string.tasks,
                                iconRes = R.drawable.tasks,
                                selectedIconRes = R.drawable.tasks_fill,
                                page = Route.Page.Tasks
                            )
                        },
                        selectedPage = selectedPage,
                        navigate = navigate
                    ) {

                    }
                    CustomNavigationBarItem(
                        barItem = remember {
                            BarItem(
                                title = R.string.settings,
                                iconRes = R.drawable.settings,
                                selectedIconRes = R.drawable.settings_fill,
                                page = Route.Page.Settings
                            )
                        },
                        selectedPage = selectedPage,
                        showBadge = appState.updatesAvailable,
                        navigate = navigate
                    ) {
                        appUiState.scope.launch {
                            appUiState.settingsListState.animateScrollToItem(0)
                        }
                    }
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
                        currentDateTime = hourlyDateTime,
                        scheduleViewModel = scheduleViewModel,
                        appBackStack = appUiState.appBackStack,
                        isDarkTheme = appSettings.decorPreferences.theme.isDarkTheme(),
                        usedPhoto = appSettings.usedImageInReview,
                        externalPadding = padding
                    )
                }

                entry<Route.Page.Schedule> {
                    ScreenSchedule(
                        importLauncher = importLauncher,
                        appUiState = appUiState,

                        currentState = currentState,
                        namedScheduleState = namedScheduleState,
                        scheduleState = scheduleState,
                        hourlyDateTime = hourlyDateTime,

                        scheduleCalendarState = scheduleCalendarState,
                        scheduleListState = scheduleListState,
                        appSettings = appSettings,
                        scheduleViewModel = scheduleViewModel,
                        preferencesViewModel = preferencesViewModel,
                        externalPadding = padding
                    )
                }

                entry<Route.Page.Tasks> {
                    TasksScreen(
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
                        importLauncher = importLauncher,
                        externalPadding = padding
                    )
                }
            }
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
    hourlyDateTime: LocalDateTime
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

                entry<Route.Dialog.EventDialog> { dialog ->
                    EventDialog(
                        eventDialog = dialog,
                        currentDateTime = hourlyDateTime,
                        fetchNamedSchedule = { name, apiId, type ->
                            appUiState.appBackStack.navigateToStartRage()
                            appUiState.appBackStack.onBack()
                            scheduleViewModel.fetchNamedSchedule(
                                name, apiId, type
                            )
                        },
                        updateEventComment = { scheduleId, event, dateTime, comment ->
                            scheduleViewModel.updateEventComment(
                                scheduleId,
                                event,
                                dateTime,
                                comment
                            )
                        },
                        updateEventTag = { scheduleId, event, dateTime, tag ->
                            scheduleViewModel.updateEventTag(
                                scheduleId,
                                event,
                                dateTime,
                                tag
                            )
                        },
                        deleteEvent = { namedScheduleId, eventId ->
                            scheduleViewModel.eventAction(
                                namedScheduleId,
                                EventAction.Delete(eventId)
                            )
                            appUiState.appBackStack.onBack()
                        },
                        hideEvent = { namedScheduleId, eventId ->
                            scheduleViewModel.eventAction(
                                namedScheduleId,
                                EventAction.UpdateHidden(eventId, true)
                            )
                        },
                        navigateToEditEventDialog = { editDialog ->
                            appUiState.appBackStack.openDialog(editDialog)
                        }
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.NewsList> {
                    NewsScreen(
                        onGetNewsById = { id ->
                            appUiState.appBackStack.openDialog(
                                Route.Dialog.NewsDialog(id)
                            )
                        },
                        newsGridListState = appUiState.newsListState
                    )
                }

                entry<Route.Dialog.NewsDialog> { dialog ->
                    NewsDialog(
                        newsDialog = dialog
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.AddEditEventDialog> { dialog ->
                    AddEditEventDialog(
                        addEditEventDialog = dialog,
                        currentDate = hourlyDateTime.toLocalDate(),
                        scope = appUiState.scope,
                        addEvent = { namedScheduleId, event ->
                            scheduleViewModel.eventAction(
                                namedScheduleId, EventAction.Add(event)
                            )
                        },
                        editEvent = { namedScheduleId, updatedEvent ->
                            scheduleViewModel.eventAction(
                                namedScheduleId,
                                EventAction.Update(updatedEvent, dialog.updatableEvent)
                            )
                        }
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }
                entry<Route.Dialog.SearchDialog> {
                    SearchDialog { name, apiId, type ->
                        scheduleViewModel.fetchNamedSchedule(
                            name, apiId, type
                        )
                        appUiState.appBackStack.openPage(Route.Page.Schedule)
                        appUiState.appBackStack.onBack()
                    }
                }
                entry<Route.Dialog.CurriculumDialog> {
                    CurriculumDialog()
                }
                entry<Route.Dialog.AddScheduleDialog> {
                    AddScheduleDialog(
                        addSchedule = { name, startDate, endDate ->
                            appUiState.appBackStack.navigateToStartRage()
                            appUiState.appBackStack.onBack()
                            scheduleViewModel.addCustomNamedSchedule(
                                name.trim(),
                                startDate,
                                endDate
                            )
                        }
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }
                entry<Route.Dialog.RenameNamedScheduleDialog> { dialog ->
                    RenameDialog(
                        renameDialog = dialog,
                        renameSchedule = { newName ->
                            scheduleViewModel.renameNamedSchedule(
                                namedScheduleId = dialog.namedScheduleId,
                                currentName = dialog.namedScheduleFullName,
                                newName = newName.trim()
                            )
                            appUiState.appBackStack.onBack()
                        }
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }
                entry<Route.Dialog.HiddenEventsDialog> { dialog ->
                    HiddenEventsDialog(
                        hiddenEventsDialog = dialog,
                        hiddenEvents = scheduleState.scheduleUiDto?.hiddenEvents ?: listOf(),
                        onShowEvent = { eventId ->
                            scheduleViewModel.eventAction(
                                dialog.namedScheduleId,
                                EventAction.UpdateHidden(eventId, false)
                            )
                        }
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }
            }
        )
    }
}